package com.easyservice.backend.infrastructure;

import com.easyservice.backend.service.RandomNumberGenerator;
import org.springframework.stereotype.Service;

@Service
public class FakeRandomNumberGenerator implements RandomNumberGenerator {
    private Integer fixedValue = null;

    public void setFixedValue(int fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public int nextInt(int bound) {
        if (fixedValue != null) {
            return fixedValue % bound;
        }
        return 0; // Default deterministic value
    }
}
