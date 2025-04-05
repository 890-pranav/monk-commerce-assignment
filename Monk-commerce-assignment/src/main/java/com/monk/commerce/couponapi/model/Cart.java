package com.monk.commerce.couponapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String cartId; // External reference ID
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
    
    public double calculateTotalPrice() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
} 