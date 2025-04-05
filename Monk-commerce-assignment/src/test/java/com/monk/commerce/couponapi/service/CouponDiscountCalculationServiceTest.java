package com.monk.commerce.couponapi.service;

import com.monk.commerce.couponapi.dto.ApplicableCouponDTO;
import com.monk.commerce.couponapi.exception.CouponNotApplicableException;
import com.monk.commerce.couponapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouponDiscountCalculationServiceTest {

    private CouponDiscountCalculationService service;
    private Cart cart;
    private Coupon cartWiseCoupon;
    private Coupon productWiseCoupon;
    private Coupon bxgyCoupon;

    @BeforeEach
    void setUp() {
        service = new CouponDiscountCalculationService();
        
        // Sample cart
        cart = new Cart();
        cart.setItems(Arrays.asList(
                new CartItem(1L, 6, 50, 0), // Total: 300
                new CartItem(2L, 3, 30, 0), // Total: 90
                new CartItem(3L, 2, 25, 0)  // Total: 50
        ));
        
        // Sample coupons
        cartWiseCoupon = Coupon.builder()
                .id(1L)
                .type(CouponType.CART_WISE)
                .code("CART10")
                .description("10% off on carts over Rs. 100")
                .threshold(100.0)
                .discountPercentage(10.0)
                .maxDiscount(50.0)
                .active(true)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .build();
        
        productWiseCoupon = Coupon.builder()
                .id(2L)
                .type(CouponType.PRODUCT_WISE)
                .code("PROD20")
                .description("20% off on Product 1")
                .productId(1L)
                .productDiscountPercentage(20.0)
                .active(true)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .build();
        
        bxgyCoupon = Coupon.builder()
                .id(3L)
                .type(CouponType.BXGY)
                .code("B2G1")
                .description("Buy 2 get 1 free")
                .buyProducts(Arrays.asList(
                        new BuyProduct(1L, 2),
                        new BuyProduct(2L, 2)
                ))
                .getProducts(Arrays.asList(
                        new GetProduct(3L, 1)
                ))
                .repetitionLimit(2)
                .active(true)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .build();
    }

    @Test
    void calculateApplicableCoupons() {
        List<Coupon> coupons = Arrays.asList(cartWiseCoupon, productWiseCoupon, bxgyCoupon);
        List<ApplicableCouponDTO> applicableCoupons = service.calculateApplicableCoupons(coupons, cart);
        
        assertEquals(3, applicableCoupons.size());
    }

    @Test
    void calculateCartWiseDiscount() {
        double discount = service.calculateDiscount(cartWiseCoupon, cart);
        
        // Cart total: 300 + 90 + 50 = 440
        // 10% discount: 44
        // Maximum discount: 50
        // Expected discount: 44
        assertEquals(44.0, discount, 0.01);
    }

    @Test
    void calculateProductWiseDiscount() {
        double discount = service.calculateDiscount(productWiseCoupon, cart);
        
        // Product 1 total: 6 * 50 = 300
        // 20% discount: 60
        assertEquals(60.0, discount, 0.01);
    }

    @Test
    void calculateBxGyDiscount() {
        double discount = service.calculateDiscount(bxgyCoupon, cart);
        
        // Buy: 2 of Product 1 and 2 of Product 2
        // Get: 1 of Product 3 for free
        // Product 3 price: 25
        // With 6 of Product 1 and 3 of Product 2, we can apply this twice
        // Expected discount: 2 * 25 = 50
        assertEquals(50.0, discount, 0.01);
    }

    @Test
    void applyCoupon() {
        Cart updatedCart = service.applyCoupon(cartWiseCoupon, cart);
        
        assertNotNull(updatedCart);
        assertEquals(440.0, updatedCart.getTotalPrice(), 0.01);
        assertEquals(44.0, updatedCart.getTotalDiscount(), 0.01);
        assertEquals(396.0, updatedCart.getFinalPrice(), 0.01);
    }

    @Test
    void couponNotApplicable() {
        Coupon highThresholdCoupon = Coupon.builder()
                .type(CouponType.CART_WISE)
                .threshold(1000.0)
                .discountPercentage(10.0)
                .build();
        
        assertThrows(CouponNotApplicableException.class, () -> {
            service.calculateDiscount(highThresholdCoupon, cart);
        });
    }
} 