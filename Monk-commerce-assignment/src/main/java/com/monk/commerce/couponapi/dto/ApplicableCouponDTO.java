package com.monk.commerce.couponapi.dto;

import com.monk.commerce.couponapi.model.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicableCouponDTO {
    private Long couponId;
    private CouponType type;
    private double discount;
} 