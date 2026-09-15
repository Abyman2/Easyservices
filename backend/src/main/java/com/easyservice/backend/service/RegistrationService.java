package com.easyservice.backend.service;

import com.easyservice.backend.dto.RegisterRequest;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class RegistrationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserRepository userRepository;
    private final IdentityVerificationService identityVerificationService;

    public RegistrationService(
            UserRepository userRepository,
            IdentityVerificationService identityVerificationService) {
        this.userRepository = userRepository;
        this.identityVerificationService = identityVerificationService;
    }

    public User register(RegisterRequest request) {
        validateRequest(request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone is already registered.");
        }

        if (!identityVerificationService.verify(
                request.getCustomerType(),
                request.getIdentityType(),
                request.getIdentityValue())) {
            throw new IllegalArgumentException("Identity verification failed.");
        }

        User user = new User(
                UUID.randomUUID().toString(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getCountry(),
                request.getCustomerType(),
                request.getIdentityType(),
                request.getIdentityValue(),
                IdentityStatus.VERIFIED,
                BigDecimal.ZERO
        );

        return userRepository.save(user);
    }

    private void validateRequest(RegisterRequest request) {
        if (request == null || isBlank(request.getFullName())) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (isBlank(request.getEmail()) || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        if (isBlank(request.getPhone())) {
            throw new IllegalArgumentException("Phone is required.");
        }
        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (isBlank(request.getCountry())) {
            throw new IllegalArgumentException("Country is required.");
        }
        if (request.getCustomerType() == null) {
            throw new IllegalArgumentException("Customer type is required.");
        }
        if (request.getIdentityType() == null) {
            throw new IllegalArgumentException("Identity type is required.");
        }
        if (isBlank(request.getIdentityValue())) {
            throw new IllegalArgumentException("Identity value is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
