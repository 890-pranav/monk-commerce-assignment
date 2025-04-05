package com.monk.commerce.couponapi.util;

import com.monk.commerce.couponapi.dto.CartDTO;
import com.monk.commerce.couponapi.dto.CartItemDTO;
import com.monk.commerce.couponapi.model.Cart;
import com.monk.commerce.couponapi.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    public static CartDTO toCartDTO(Cart cart) {
        if (cart == null) return null;
        
        return CartDTO.builder()
                .id(cart.getId())
                .cartId(cart.getCartId())
                .items(toCartItemDTOs(cart.getItems()))
                .totalPrice(cart.getTotalPrice())
                .totalDiscount(cart.getTotalDiscount())
                .finalPrice(cart.getFinalPrice())
                .build();
    }
    
    public static List<CartItemDTO> toCartItemDTOs(List<CartItem> items) {
        if (items == null) return null;
        
        return items.stream()
                .map(CartMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }
    
    public static CartItemDTO toCartItemDTO(CartItem item) {
        if (item == null) return null;
        
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalDiscount(item.getTotalDiscount())
                .subtotal(item.getSubtotal())
                .build();
    }
} 