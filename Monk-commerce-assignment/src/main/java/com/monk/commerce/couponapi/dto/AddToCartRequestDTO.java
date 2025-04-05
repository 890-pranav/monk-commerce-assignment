package com.monk.commerce.couponapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequestDTO {
    private Long productId;
    private int quantity;
    private double price;
} 