package com.monk.commerce.couponapi.service;

import com.monk.commerce.couponapi.dto.CouponRequestDTO;
import com.monk.commerce.couponapi.exception.CouponNotFoundException;
import com.monk.commerce.couponapi.model.Coupon;
import com.monk.commerce.couponapi.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon createCoupon(CouponRequestDTO couponRequestDTO) {
        Coupon coupon = new Coupon();
        
        // Set common properties
        coupon.setType(couponRequestDTO.getType());
        coupon.setCode(couponRequestDTO.getCode());
        coupon.setDescription(couponRequestDTO.getDescription());
        coupon.setExpirationDate(couponRequestDTO.getExpirationDate());
        
        // Set type-specific properties
        switch (couponRequestDTO.getType()) {
            case CART_WISE:
                coupon.setThreshold(couponRequestDTO.getThreshold());
                coupon.setDiscountPercentage(couponRequestDTO.getDiscountPercentage());
                coupon.setMaxDiscount(couponRequestDTO.getMaxDiscount());
                break;
            case PRODUCT_WISE:
                coupon.setProductId(couponRequestDTO.getProductId());
                coupon.setProductDiscountPercentage(couponRequestDTO.getProductDiscountPercentage());
                break;
            case BXGY:
                coupon.setBuyProducts(couponRequestDTO.getBuyProducts());
                coupon.setGetProducts(couponRequestDTO.getGetProducts());
                coupon.setRepetitionLimit(couponRequestDTO.getRepetitionLimit());
                break;
        }
        
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));
    }

    public Coupon updateCoupon(Long id, CouponRequestDTO couponRequestDTO) {
        Coupon existingCoupon = getCouponById(id);
        
        // Update common properties
        existingCoupon.setCode(couponRequestDTO.getCode());
        existingCoupon.setDescription(couponRequestDTO.getDescription());
        existingCoupon.setExpirationDate(couponRequestDTO.getExpirationDate());
        
        // Update type-specific properties
        switch (couponRequestDTO.getType()) {
            case CART_WISE:
                existingCoupon.setThreshold(couponRequestDTO.getThreshold());
                existingCoupon.setDiscountPercentage(couponRequestDTO.getDiscountPercentage());
                existingCoupon.setMaxDiscount(couponRequestDTO.getMaxDiscount());
                break;
            case PRODUCT_WISE:
                existingCoupon.setProductId(couponRequestDTO.getProductId());
                existingCoupon.setProductDiscountPercentage(couponRequestDTO.getProductDiscountPercentage());
                break;
            case BXGY:
                existingCoupon.setBuyProducts(couponRequestDTO.getBuyProducts());
                existingCoupon.setGetProducts(couponRequestDTO.getGetProducts());
                existingCoupon.setRepetitionLimit(couponRequestDTO.getRepetitionLimit());
                break;
        }
        
        return couponRepository.save(existingCoupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }

    @Transactional(readOnly = true)
    public List<Coupon> getActiveCoupons() {
        return couponRepository.findByActiveIsTrueAndExpirationDateIsAfterOrExpirationDateIsNull(LocalDateTime.now());
    }
} 