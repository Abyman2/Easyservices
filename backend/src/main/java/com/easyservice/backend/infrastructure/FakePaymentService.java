package com.easyservice.backend.infrastructure;

import com.easyservice.backend.model.User;
import com.easyservice.backend.repository.UserRepository;
import com.easyservice.backend.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class FakePaymentService implements PaymentService {

    private final UserRepository userRepository;
    private boolean simulateError = false;

    public FakePaymentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setSimulateError(boolean simulateError) {
        this.simulateError = simulateError;
    }

    @Override
    public PaymentResult processPayment(String userId, BigDecimal amount) {
        if (simulateError) {
            return new PaymentResult(PaymentStatus.ERROR, "Simulated Payment Gateway Error");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(PaymentStatus.DECLINED, "Invalid Payment Amount");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new PaymentResult(PaymentStatus.DECLINED, "User Not Found");
        }

        User user = userOpt.get();
        BigDecimal balance = user.getBalance();

        if (balance == null || balance.compareTo(amount) < 0) {
            return new PaymentResult(PaymentStatus.DECLINED, "Insufficient Simulated Balance");
        }

        // Deduct balance and update user
        user.setBalance(balance.subtract(amount));
        userRepository.save(user);

        return new PaymentResult(PaymentStatus.SUCCESS, "Payment Processed Successfully");
    }
}
