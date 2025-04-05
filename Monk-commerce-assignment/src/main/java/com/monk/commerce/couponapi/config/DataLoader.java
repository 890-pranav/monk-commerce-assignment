package com.monk.commerce.couponapi.config;

import com.monk.commerce.couponapi.model.*;
import com.monk.commerce.couponapi.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(@Autowired CouponRepository couponRepository) {
        return args -> {
            // Create a cart-wise coupon
            Coupon cartWiseCoupon = Coupon.builder()
                    .type(CouponType.CART_WISE)
                    .code("CART10")
                    .description("10% off on carts over Rs. 100")
                    .threshold(100.0)
                    .discountPercentage(10.0)
                    .maxDiscount(50.0)
                    .active(true)
                    .expirationDate(LocalDateTime.now().plusMonths(1))
                    .build();
            
            // Create a product-wise coupon
            Coupon productWiseCoupon = Coupon.builder()
                    .type(CouponType.PRODUCT_WISE)
                    .code("PROD20")
                    .description("20% off on Product 1")
                    .productId(1L)
                    .productDiscountPercentage(20.0)
                    .active(true)
                    .expirationDate(LocalDateTime.now().plusMonths(1))
                    .build();
            
            // Create a BxGy coupon
            Coupon bxgyCoupon = Coupon.builder()
                    .type(CouponType.BXGY)
                    .code("B2G1")
                    .description("Buy 2 get 1 free")
                    .buyProducts(Arrays.asList(
                            new BuyProduct(1L, 2),
                            new BuyProduct(2L, 2)
                    ))
                    .getProducts(Arrays.asList(
                            new GetProduct(3L, 1)
                    ))
                    .repetitionLimit(2)
                    .active(true)
                    .expirationDate(LocalDateTime.now().plusMonths(1))
                    .build();
            
            // Save coupons
            couponRepository.saveAll(Arrays.asList(cartWiseCoupon, productWiseCoupon, bxgyCoupon));
        };
    }
} 