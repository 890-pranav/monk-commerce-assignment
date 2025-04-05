package com.monk.commerce.couponapi.service;

import com.monk.commerce.couponapi.dto.*;
import com.monk.commerce.couponapi.exception.CartNotFoundException;
import com.monk.commerce.couponapi.model.Cart;
import com.monk.commerce.couponapi.model.CartItem;
import com.monk.commerce.couponapi.repository.CartRepository;
import com.monk.commerce.couponapi.util.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional
    public CartResponseDTO createCart() {
        Cart cart = new Cart();
        cart.setCartId(UUID.randomUUID().toString());
        Cart savedCart = cartRepository.save(cart);
        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(savedCart))
                .message("Cart created successfully")
                .build();
    }

    @Transactional
    public CartResponseDTO addToCart(String cartId, AddToCartRequestDTO request) {
        Cart cart = getCartByCartId(cartId);

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(request.getPrice())
                    .totalDiscount(0.0)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        updateCartTotals(cart);
        Cart updatedCart = cartRepository.save(cart);

        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(updatedCart))
                .message("Item added to cart successfully")
                .build();
    }

    @Transactional
    public CartResponseDTO updateCartItemQuantity(String cartId, Long productId, int quantity) {
        Cart cart = getCartByCartId(cartId);

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        updateCartTotals(cart);
        Cart updatedCart = cartRepository.save(cart);

        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(updatedCart))
                .message("Cart item quantity updated successfully")
                .build();
    }

    @Transactional
    public CartResponseDTO removeFromCart(String cartId, Long productId) {
        Cart cart = getCartByCartId(cartId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        
        updateCartTotals(cart);
        Cart updatedCart = cartRepository.save(cart);

        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(updatedCart))
                .message("Item removed from cart successfully")
                .build();
    }

    @Transactional
    public CartResponseDTO clearCart(String cartId) {
        Cart cart = getCartByCartId(cartId);
        cart.getItems().clear();
        updateCartTotals(cart);
        Cart updatedCart = cartRepository.save(cart);

        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(updatedCart))
                .message("Cart cleared successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCart(String cartId) {
        Cart cart = getCartByCartId(cartId);
        return CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(cart))
                .message("Cart retrieved successfully")
                .build();
    }

    private Cart getCartByCartId(String cartId) {
        return cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));
    }

    private void updateCartTotals(Cart cart) {
        double totalPrice = cart.calculateTotalPrice();
        double totalDiscount = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalDiscount)
                .sum();
        
        cart.setTotalPrice(totalPrice);
        cart.setTotalDiscount(totalDiscount);
        cart.setFinalPrice(totalPrice - totalDiscount);
    }
} 