package com.monk.commerce.couponapi.controller;

import com.monk.commerce.couponapi.dto.AddToCartRequestDTO;
import com.monk.commerce.couponapi.dto.CartResponseDTO;
import com.monk.commerce.couponapi.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create")
    public ResponseEntity<CartResponseDTO> createCart() {
        CartResponseDTO response = cartService.createCart();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> getCartItems(@PathVariable String cartId) {
        CartResponseDTO response = cartService.getCart(cartId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            @PathVariable String cartId,
            @RequestBody AddToCartRequestDTO request) {
        CartResponseDTO response = cartService.addToCart(cartId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(
            @PathVariable String cartId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        CartResponseDTO response = cartService.updateCartItemQuantity(cartId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @PathVariable String cartId,
            @PathVariable Long productId) {
        CartResponseDTO response = cartService.removeFromCart(cartId, productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> clearCart(@PathVariable String cartId) {
        CartResponseDTO response = cartService.clearCart(cartId);
        return ResponseEntity.ok(response);
    }
} 