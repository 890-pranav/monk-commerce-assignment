package com.monk.commerce.couponapi.service;

import com.monk.commerce.couponapi.dto.ApplicableCouponDTO;
import com.monk.commerce.couponapi.exception.CouponNotApplicableException;
import com.monk.commerce.couponapi.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CouponDiscountCalculationService {

    public List<ApplicableCouponDTO> calculateApplicableCoupons(List<Coupon> coupons, Cart cart) {
        List<ApplicableCouponDTO> applicableCoupons = new ArrayList<>();
        
        for (Coupon coupon : coupons) {
            // Check if coupon is active and not expired
            if (!coupon.isActive() || (coupon.getExpirationDate() != null && 
                    coupon.getExpirationDate().isBefore(LocalDateTime.now()))) {
                continue;
            }
            
            try {
                double discount = calculateDiscount(coupon, cart);
                if (discount > 0) {
                    applicableCoupons.add(ApplicableCouponDTO.builder()
                            .couponId(coupon.getId())
                            .type(coupon.getType())
                            .discount(discount)
                            .build());
                }
            } catch (CouponNotApplicableException e) {
                // Coupon not applicable, skip
            }
        }
        
        return applicableCoupons;
    }

    public double calculateDiscount(Coupon coupon, Cart cart) {
        switch (coupon.getType()) {
            case CART_WISE:
                return calculateCartWiseDiscount(coupon, cart);
            case PRODUCT_WISE:
                return calculateProductWiseDiscount(coupon, cart);
            case BXGY:
                return calculateBxGyDiscount(coupon, cart);
            default:
                return 0;
        }
    }

    private double calculateCartWiseDiscount(Coupon coupon, Cart cart) {
        double cartTotal = cart.calculateTotalPrice();
        
        if (cartTotal < coupon.getThreshold()) {
            throw new CouponNotApplicableException("Cart total does not meet threshold for coupon");
        }
        
        double discountAmount = cartTotal * (coupon.getDiscountPercentage() / 100);
        
        // Apply maximum discount if set
        if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
            discountAmount = coupon.getMaxDiscount();
        }
        
        return discountAmount;
    }

    private double calculateProductWiseDiscount(Coupon coupon, Cart cart) {
        double totalDiscount = 0;
        
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(coupon.getProductId())) {
                totalDiscount += item.getPrice() * item.getQuantity() * 
                        (coupon.getProductDiscountPercentage() / 100);
            }
        }
        
        if (totalDiscount == 0) {
            throw new CouponNotApplicableException("Product not found in cart for product-wise coupon");
        }
        
        return totalDiscount;
    }

    private double calculateBxGyDiscount(Coupon coupon, Cart cart) {
        // Calculate how many products from "buy" array are in the cart
        Map<Long, Integer> buyProductCounts = countProductsInCart(coupon.getBuyProducts(), cart);
        
        // Calculate how many complete sets of "buy" products are in the cart
        int minSets = calculateMinSets(buyProductCounts, coupon.getBuyProducts());
        
        if (minSets == 0) {
            throw new CouponNotApplicableException("Not enough products in cart for BxGy coupon");
        }
        
        // Apply repetition limit if set
        if (coupon.getRepetitionLimit() != null && minSets > coupon.getRepetitionLimit()) {
            minSets = coupon.getRepetitionLimit();
        }
        
        // Calculate the total discount based on the free products
        double totalDiscount = 0;
        Map<Long, CartItem> productIdToCartItem = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, item -> item));
        
        for (GetProduct getProduct : coupon.getGetProducts()) {
            CartItem item = productIdToCartItem.get(getProduct.getProductId());
            if (item != null) {
                int freeQuantity = getProduct.getQuantity() * minSets;
                // Limit free quantity to actual quantity in cart
                freeQuantity = Math.min(freeQuantity, item.getQuantity());
                totalDiscount += item.getPrice() * freeQuantity;
            }
        }
        
        return totalDiscount;
    }

    private Map<Long, Integer> countProductsInCart(List<BuyProduct> buyProducts, Cart cart) {
        Map<Long, Integer> counts = new HashMap<>();
        
        // Initialize counts for all buy products
        for (BuyProduct buyProduct : buyProducts) {
            counts.put(buyProduct.getProductId(), 0);
        }
        
        // Count products in cart
        for (CartItem item : cart.getItems()) {
            if (counts.containsKey(item.getProductId())) {
                counts.put(item.getProductId(), item.getQuantity());
            }
        }
        
        return counts;
    }

    private int calculateMinSets(Map<Long, Integer> productCounts, List<BuyProduct> buyProducts) {
        int minSets = Integer.MAX_VALUE;
        
        for (BuyProduct buyProduct : buyProducts) {
            int productCount = productCounts.getOrDefault(buyProduct.getProductId(), 0);
            int sets = productCount / buyProduct.getQuantity();
            minSets = Math.min(minSets, sets);
        }
        
        return minSets == Integer.MAX_VALUE ? 0 : minSets;
    }

    public Cart applyCoupon(Coupon coupon, Cart cart) {
        // Create a deep copy of the cart to avoid modifying the original
        Cart updatedCart = new Cart();
        List<CartItem> updatedItems = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            updatedItems.add(new CartItem(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice(),
                    0.0  // Initial total discount
            ));
        }
        
        updatedCart.setItems(updatedItems);
        
        // Apply the coupon based on its type
        switch (coupon.getType()) {
            case CART_WISE:
                applyCartWiseCoupon(coupon, updatedCart);
                break;
            case PRODUCT_WISE:
                applyProductWiseCoupon(coupon, updatedCart);
                break;
            case BXGY:
                applyBxGyCoupon(coupon, updatedCart);
                break;
        }
        
        // Calculate the updated cart totals
        double totalPrice = updatedCart.calculateTotalPrice();
        double totalDiscount = updatedCart.getItems().stream()
                .mapToDouble(CartItem::getTotalDiscount)
                .sum();
        
        updatedCart.setTotalPrice(totalPrice);
        updatedCart.setTotalDiscount(totalDiscount);
        updatedCart.setFinalPrice(totalPrice - totalDiscount);
        
        return updatedCart;
    }

    private void applyCartWiseCoupon(Coupon coupon, Cart cart) {
        double cartTotal = cart.calculateTotalPrice();
        
        if (cartTotal < coupon.getThreshold()) {
            throw new CouponNotApplicableException("Cart total does not meet threshold for coupon");
        }
        
        double discountAmount = cartTotal * (coupon.getDiscountPercentage() / 100);
        
        // Apply maximum discount if set
        if (coupon.getMaxDiscount() != null && discountAmount > coupon.getMaxDiscount()) {
            discountAmount = coupon.getMaxDiscount();
        }
        
        // Distribute the discount proportionally among all items
        double totalCartValue = cart.calculateTotalPrice();
        
        for (CartItem item : cart.getItems()) {
            double itemValue = item.getPrice() * item.getQuantity();
            double proportion = itemValue / totalCartValue;
            item.setTotalDiscount(discountAmount * proportion);
        }
    }

    private void applyProductWiseCoupon(Coupon coupon, Cart cart) {
        boolean couponApplied = false;
        
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(coupon.getProductId())) {
                double discount = item.getPrice() * item.getQuantity() * 
                        (coupon.getProductDiscountPercentage() / 100);
                item.setTotalDiscount(discount);
                couponApplied = true;
            }
        }
        
        if (!couponApplied) {
            throw new CouponNotApplicableException("Product not found in cart for product-wise coupon");
        }
    }

    private void applyBxGyCoupon(Coupon coupon, Cart cart) {
        // Calculate how many products from "buy" array are in the cart
        Map<Long, Integer> buyProductCounts = countProductsInCart(coupon.getBuyProducts(), cart);
        
        // Calculate how many complete sets of "buy" products are in the cart
        int minSets = calculateMinSets(buyProductCounts, coupon.getBuyProducts());
        
        if (minSets == 0) {
            throw new CouponNotApplicableException("Not enough products in cart for BxGy coupon");
        }
        
        // Apply repetition limit if set
        if (coupon.getRepetitionLimit() != null && minSets > coupon.getRepetitionLimit()) {
            minSets = coupon.getRepetitionLimit();
        }
        
        // Map to keep track of how many items we're marking as free
        Map<Long, Integer> freeQuantities = new HashMap<>();
        
        // Initialize free quantities
        for (GetProduct getProduct : coupon.getGetProducts()) {
            freeQuantities.put(getProduct.getProductId(), getProduct.getQuantity() * minSets);
        }
        
        // Apply discounts to cart items
        for (CartItem item : cart.getItems()) {
            if (freeQuantities.containsKey(item.getProductId())) {
                int freeQty = Math.min(freeQuantities.get(item.getProductId()), item.getQuantity());
                item.setTotalDiscount(item.getPrice() * freeQty);
            }
        }
    }
}