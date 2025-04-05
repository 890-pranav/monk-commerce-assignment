package com.monk.commerce.couponapi.controller;

import com.monk.commerce.couponapi.dto.ApplicableCouponDTO;
import com.monk.commerce.couponapi.dto.ApplicableCouponsResponseDTO;
import com.monk.commerce.couponapi.dto.CartRequestDTO;
import com.monk.commerce.couponapi.dto.CartResponseDTO;
import com.monk.commerce.couponapi.model.Cart;
import com.monk.commerce.couponapi.model.Coupon;
import com.monk.commerce.couponapi.service.CouponDiscountCalculationService;
import com.monk.commerce.couponapi.service.CouponService;
import com.monk.commerce.couponapi.util.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class CouponApplicationController {

    private final CouponService couponService;
    private final CouponDiscountCalculationService calculationService;

    @Autowired
    public CouponApplicationController(
            CouponService couponService,
            CouponDiscountCalculationService calculationService) {
        this.couponService = couponService;
        this.calculationService = calculationService;
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<ApplicableCouponsResponseDTO> getApplicableCoupons(
            @RequestBody CartRequestDTO cartRequest) {
        // Get all active coupons
        List<Coupon> activeCoupons = couponService.getActiveCoupons();
        
        // Calculate applicable coupons for the cart
        List<ApplicableCouponDTO> applicableCoupons = 
                calculationService.calculateApplicableCoupons(activeCoupons, cartRequest.getCart());
        
        ApplicableCouponsResponseDTO response = ApplicableCouponsResponseDTO.builder()
                .applicableCoupons(applicableCoupons)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartResponseDTO> applyCoupon(
            @PathVariable Long id,
            @RequestBody CartRequestDTO cartRequest) {
        // Get the coupon
        Coupon coupon = couponService.getCouponById(id);
        
        // Apply the coupon to the cart
        Cart updatedCart = calculationService.applyCoupon(coupon, cartRequest.getCart());
        
        CartResponseDTO response = CartResponseDTO.builder()
                .cart(CartMapper.toCartDTO(updatedCart))
                .message("Coupon applied successfully")
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 