package com.monk.commerce.couponapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productId;
    private int quantity;
    private double price;
    private double totalDiscount;
    
    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    // Add this constructor for the CouponDiscountCalculationService
    public CartItem(Long productId, int quantity, double price, double totalDiscount) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalDiscount = totalDiscount;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
} 