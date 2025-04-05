package com.monk.commerce.couponapi.dto;

import com.monk.commerce.couponapi.model.Cart;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartRequestDTO {
    public Cart cart;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}