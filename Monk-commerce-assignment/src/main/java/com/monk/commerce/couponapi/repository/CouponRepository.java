package com.monk.commerce.couponapi.repository;

import com.monk.commerce.couponapi.model.Coupon;
import com.monk.commerce.couponapi.model.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByTypeAndActiveIsTrue(CouponType type);
    
    List<Coupon> findByActiveIsTrueAndExpirationDateIsAfterOrExpirationDateIsNull(LocalDateTime now);
    
    List<Coupon> findByProductIdAndActiveIsTrue(Long productId);
} 