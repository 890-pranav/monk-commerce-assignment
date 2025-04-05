package com.monk.commerce.couponapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private CouponType type;
    
    private String code;
    private String description;
    
    // For cart-wise coupon
    private Double threshold;
    private Double discountPercentage;
    private Double maxDiscount;
    
    // For product-wise coupon
    private Long productId;
    private Double productDiscountPercentage;
    
    // For BxGy coupon
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coupon_buy_products", 
            joinColumns = @JoinColumn(name = "coupon_id"))
    @Builder.Default
    private List<BuyProduct> buyProducts = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coupon_get_products", 
            joinColumns = @JoinColumn(name = "coupon_id"))
    @Builder.Default
    private List<GetProduct> getProducts = new ArrayList<>();
    
    private Integer repetitionLimit;
    
    // Expiration date (Bonus feature)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;
    
    private boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == false) {
            active = true;
        }
    }
} 