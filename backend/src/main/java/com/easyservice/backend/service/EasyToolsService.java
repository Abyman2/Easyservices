package com.easyservice.backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EasyToolsService {

    private final RandomNumberGenerator randomNumberGenerator;

    public EasyToolsService(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public BigDecimal calculateEqualSplit(BigDecimal totalAmount, int numberOfPeople) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        if (numberOfPeople <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than zero");
        }
        return totalAmount.divide(BigDecimal.valueOf(numberOfPeople), 2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> calculateItemizedSplit(Map<String, List<BigDecimal>> userItems) {
        if (userItems == null || userItems.isEmpty()) {
            throw new IllegalArgumentException("User item mapping cannot be empty");
        }

        Map<String, BigDecimal> userTotals = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : userItems.entrySet()) {
            BigDecimal sum = BigDecimal.ZERO;
            if (entry.getValue() != null) {
                for (BigDecimal itemPrice : entry.getValue()) {
                    if (itemPrice != null && itemPrice.compareTo(BigDecimal.ZERO) > 0) {
                        sum = sum.add(itemPrice);
                    }
                }
            }
            userTotals.put(entry.getKey(), sum);
        }
        return userTotals;
    }

    public String selectRandomPayer(List<String> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants list cannot be empty");
        }
        int selectedIndex = randomNumberGenerator.nextInt(participants.size());
        return participants.get(selectedIndex);
    }
}
