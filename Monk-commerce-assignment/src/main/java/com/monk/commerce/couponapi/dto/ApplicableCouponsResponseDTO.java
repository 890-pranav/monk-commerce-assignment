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
public class ApplicableCouponsResponseDTO {
    public List<ApplicableCouponDTO> applicableCoupons;
}