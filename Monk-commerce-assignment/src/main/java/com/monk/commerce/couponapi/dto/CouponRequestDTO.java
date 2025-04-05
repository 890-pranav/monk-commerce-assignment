package com.monk.commerce.couponapi.dto;

import com.monk.commerce.couponapi.model.BuyProduct;
import com.monk.commerce.couponapi.model.CouponType;
import com.monk.commerce.couponapi.model.GetProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDTO {
    private CouponType type;
    private String code;
    private String description;
    
    // Cart-wise coupon details
    private Double threshold;
    private Double discountPercentage;
    private Double maxDiscount;
    
    // Product-wise coupon details
    private Long productId;
    private Double productDiscountPercentage;
    
    // BxGy coupon details
    private List<BuyProduct> buyProducts;
    private List<GetProduct> getProducts;
    private Integer repetitionLimit;
    
    // Expiration date
    private LocalDateTime expirationDate;
} 