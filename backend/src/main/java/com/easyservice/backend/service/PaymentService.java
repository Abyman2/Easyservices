package com.easyservice.backend.service;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentResult processPayment(String userId, BigDecimal amount);
    
    enum PaymentStatus {
        SUCCESS,
        DECLINED,
        ERROR
    }

    class PaymentResult {
        private final PaymentStatus status;
        private final String message;

        public PaymentResult(PaymentStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public PaymentStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public boolean isSuccess() { return status == PaymentStatus.SUCCESS; }
    }
}
