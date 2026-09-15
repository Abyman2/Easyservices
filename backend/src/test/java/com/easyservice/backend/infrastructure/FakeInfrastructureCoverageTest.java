package com.easyservice.backend.infrastructure;

import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.repository.InMemoryUserRepository;
import com.easyservice.backend.repository.UserRepository;
import com.easyservice.backend.service.PaymentService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FakeInfrastructureCoverageTest {

    @Test
    void paymentCoversGatewayValidationLookupBalanceAndSuccessBranches() {
        UserRepository users = new InMemoryUserRepository();
        FakePaymentService payments = new FakePaymentService(users);

        payments.setSimulateError(true);
        assertEquals(PaymentService.PaymentStatus.ERROR, payments.processPayment("missing", BigDecimal.ONE).getStatus());

        payments.setSimulateError(false);
        assertEquals(PaymentService.PaymentStatus.DECLINED, payments.processPayment("missing", BigDecimal.ONE).getStatus());
        assertEquals(PaymentService.PaymentStatus.DECLINED, payments.processPayment("missing", null).getStatus());
        assertEquals(PaymentService.PaymentStatus.DECLINED, payments.processPayment("missing", BigDecimal.ZERO).getStatus());

        User noBalance = user("no-balance", null);
        users.save(noBalance);
        assertEquals(PaymentService.PaymentStatus.DECLINED, payments.processPayment("no-balance", BigDecimal.ONE).getStatus());

        User customer = user("customer", BigDecimal.TEN);
        users.save(customer);
        assertEquals(PaymentService.PaymentStatus.DECLINED, payments.processPayment("customer", BigDecimal.valueOf(11)).getStatus());
        assertEquals(PaymentService.PaymentStatus.SUCCESS, payments.processPayment("customer", BigDecimal.valueOf(3)).getStatus());
        assertEquals(BigDecimal.valueOf(7), users.findById("customer").orElseThrow().getBalance());
    }

    @Test
    void identityAndNotificationCoverEmptyAndValidStates() {
        FakeIdentityVerificationService identity = new FakeIdentityVerificationService();
        assertEquals(false, identity.verify(null, IdentityType.FAYDA, "TEST-FAYDA-VALID"));
        assertEquals(false, identity.verify(CustomerType.ETHIOPIAN, null, "TEST-FAYDA-VALID"));
        assertEquals(false, identity.verify(CustomerType.ETHIOPIAN, IdentityType.FAYDA, " "));
        assertEquals(false, identity.verify(CustomerType.FOREIGNER, IdentityType.PASSPORT, "INVALID"));

        FakeNotificationService notifications = new FakeNotificationService();
        assertNull(notifications.getLastNotificationMessage());
        notifications.sendNotification("user@example.com", "Ready");
        assertEquals(1, notifications.getNotificationCount());
        assertEquals("Recipient: user@example.com | Message: Ready", notifications.getLastNotificationMessage());
        notifications.clear();
        assertEquals(0, notifications.getNotificationCount());
    }

    @Test
    void randomGeneratorCoversDefaultAndFixedValues() {
        FakeRandomNumberGenerator generator = new FakeRandomNumberGenerator();
        assertEquals(0, generator.nextInt(10));
        generator.setFixedValue(17);
        assertEquals(2, generator.nextInt(5));
    }

    private User user(String id, BigDecimal balance) {
        return new User(id, "Test User", id + "@example.com", "+251900000000", "Password123!",
                "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID",
                IdentityStatus.VERIFIED, balance);
    }
}
