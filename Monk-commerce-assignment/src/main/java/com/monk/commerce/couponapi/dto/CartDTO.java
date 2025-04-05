package com.monk.commerce.couponapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private String cartId;
    private List<CartItemDTO> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
} 