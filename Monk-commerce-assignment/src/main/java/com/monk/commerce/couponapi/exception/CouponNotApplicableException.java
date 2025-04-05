package com.monk.commerce.couponapi.exception;

public class CouponNotApplicableException extends RuntimeException {
    public CouponNotApplicableException(String message) {
        super(message);
    }
} 