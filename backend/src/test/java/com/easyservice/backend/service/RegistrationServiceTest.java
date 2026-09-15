package com.easyservice.backend.service;

import com.easyservice.backend.dto.RegisterRequest;
import com.easyservice.backend.infrastructure.FakeIdentityVerificationService;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(
                new InMemoryUserRepository(),
                new FakeIdentityVerificationService()
        );
    }

    @Test
    void shouldRegisterValidEthiopianCustomer() {
        User user = registrationService.register(validEthiopianRequest());

        assertNotNull(user);
        assertEquals("Abel Test", user.getFullName());
        assertEquals("abel@example.com", user.getEmail());
        assertEquals(CustomerType.ETHIOPIAN, user.getCustomerType());
    }

    @Test
    void shouldRegisterValidForeignCustomer() {
        User user = registrationService.register(validForeignRequest());

        assertNotNull(user);
        assertEquals(CustomerType.FOREIGNER, user.getCustomerType());
        assertEquals(IdentityType.PASSPORT, user.getIdentityType());
    }

    @Test
    void shouldRejectEthiopianCustomerUsingPassport() {
        RegisterRequest request = validEthiopianRequest();
        request.setIdentityType(IdentityType.PASSPORT);
        request.setIdentityValue("TEST-PASSPORT-VALID");

        assertThrows(IllegalArgumentException.class,
                () -> registrationService.register(request));
    }

    @Test
    void shouldRejectForeignCustomerUsingFayda() {
        RegisterRequest request = validForeignRequest();
        request.setIdentityType(IdentityType.FAYDA);
        request.setIdentityValue("TEST-FAYDA-VALID");

        assertThrows(IllegalArgumentException.class,
                () -> registrationService.register(request));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        registrationService.register(validEthiopianRequest());

        assertThrows(IllegalArgumentException.class,
                () -> registrationService.register(validEthiopianRequest()));
    }

    @Test
    void shouldRejectDuplicatePhone() {
        registrationService.register(validEthiopianRequest());
        RegisterRequest duplicate = validEthiopianRequest();
        duplicate.setEmail("different@example.com");

        assertThrows(IllegalArgumentException.class,
                () -> registrationService.register(duplicate));
    }

    @Test
    void shouldRejectFailedIdentityVerification() {
        RegisterRequest request = validEthiopianRequest();
        request.setIdentityValue("INVALID-ID");

        assertThrows(IllegalArgumentException.class,
                () -> registrationService.register(request));
    }

    @Test
    void shouldSetVerifiedStatusAndZeroBalanceAfterSuccessfulRegistration() {
        User user = registrationService.register(validEthiopianRequest());

        assertEquals(IdentityStatus.VERIFIED, user.getIdentityStatus());
        assertEquals(BigDecimal.ZERO, user.getBalance());
    }

    private RegisterRequest validEthiopianRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Abel Test");
        request.setEmail("abel@example.com");
        request.setPhone("+251900000000");
        request.setPassword("Password123!");
        request.setCountry("Ethiopia");
        request.setCustomerType(CustomerType.ETHIOPIAN);
        request.setIdentityType(IdentityType.FAYDA);
        request.setIdentityValue("TEST-FAYDA-VALID");
        return request;
    }

    private RegisterRequest validForeignRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Foreign Test");
        request.setEmail("foreign@example.com");
        request.setPhone("+12025550123");
        request.setPassword("Password123!");
        request.setCountry("United States");
        request.setCustomerType(CustomerType.FOREIGNER);
        request.setIdentityType(IdentityType.PASSPORT);
        request.setIdentityValue("TEST-PASSPORT-VALID");
        return request;
    }
}
