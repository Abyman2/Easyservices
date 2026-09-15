package com.easyservice.backend.repository;

import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private final UserRepository repository =
            new InMemoryUserRepository();

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = createUser();

        repository.save(user);

        Optional<User> result =
                repository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void shouldSaveAndFindUserByPhone() {
        User user = createUser();

        repository.save(user);

        Optional<User> result =
                repository.findByPhone("+251900000000");

        assertTrue(result.isPresent());
        assertEquals("+251900000000", result.get().getPhone());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {

        Optional<User> result =
                repository.findByEmail("missing@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenPhoneDoesNotExist() {

        Optional<User> result =
                repository.findByPhone("+251911111111");

        assertTrue(result.isEmpty());
    }

    private User createUser() {
        return new User(
                "user-1",
                "Test User",
                "test@example.com",
                "+251900000000",
                "password",
                "Ethiopia",
                CustomerType.ETHIOPIAN,
                IdentityType.FAYDA,
                "TEST-FAYDA-VALID",
                IdentityStatus.VERIFIED,
                BigDecimal.ZERO
        );
    }
}