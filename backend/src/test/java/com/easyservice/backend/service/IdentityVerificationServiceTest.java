package com.easyservice.backend.service;

import com.easyservice.backend.infrastructure.FakeIdentityVerificationService;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityVerificationServiceTest {

    private final IdentityVerificationService service =
            new FakeIdentityVerificationService();

    @Test
    void ethiopianCustomerWithValidFaydaShouldBeVerified() {

        boolean result = service.verify(
                CustomerType.ETHIOPIAN,
                IdentityType.FAYDA,
                "TEST-FAYDA-VALID"
        );

        assertTrue(result);
    }

    @Test
    void ethiopianCustomerWithPassportShouldBeRejected() {

        boolean result = service.verify(
                CustomerType.ETHIOPIAN,
                IdentityType.PASSPORT,
                "TEST-PASSPORT-VALID"
        );

        assertFalse(result);
    }

    @Test
    void foreignCustomerWithValidPassportShouldBeVerified() {

        boolean result = service.verify(
                CustomerType.FOREIGNER,
                IdentityType.PASSPORT,
                "TEST-PASSPORT-VALID"
        );

        assertTrue(result);
    }

    @Test
    void foreignCustomerWithFaydaShouldBeRejected() {

        boolean result = service.verify(
                CustomerType.FOREIGNER,
                IdentityType.FAYDA,
                "TEST-FAYDA-VALID"
        );

        assertFalse(result);
    }

    @Test
    void missingIdentityValueShouldBeRejected() {

        boolean result = service.verify(
                CustomerType.ETHIOPIAN,
                IdentityType.FAYDA,
                null
        );

        assertFalse(result);
    }

    @Test
    void invalidIdentityValueShouldBeRejected() {

        boolean result = service.verify(
                CustomerType.ETHIOPIAN,
                IdentityType.FAYDA,
                "INVALID-ID"
        );

        assertFalse(result);
    }
}