package com.easyservice.backend.service;

import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final IdentityVerificationService verificationService;

    public AuthService(UserRepository userRepository, IdentityVerificationService verificationService) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
    }

    public User registerUser(User user) {
        // 1. Check for null boundary states
        if (user.getCustomerType() == null || user.getIdentityType() == null) {
            throw new IllegalArgumentException("Customer type and identity type cannot be null.");
        }

        // 2. Enforce: Ethiopian -> FAYDA rule
        if (user.getCustomerType() == CustomerType.ETHIOPIAN && user.getIdentityType() != IdentityType.FAYDA) {
            throw new IllegalArgumentException("Ethiopian customers must provide a FAYDA identity.");
        }

        // 3. Enforce: Foreigner -> PASSPORT rule
        if (user.getCustomerType() == CustomerType.FOREIGNER && user.getIdentityType() != IdentityType.PASSPORT) {
            throw new IllegalArgumentException("Foreign customers must provide a PASSPORT identity.");
        }

        // 4. Trigger identity verification via the injected service
        boolean verified = verificationService.verify(
                user.getCustomerType(), user.getIdentityType(), user.getIdentityValue());
        user.setIdentityStatus(verified ? IdentityStatus.VERIFIED : IdentityStatus.UNVERIFIED);

        return userRepository.save(user);
    }
}
