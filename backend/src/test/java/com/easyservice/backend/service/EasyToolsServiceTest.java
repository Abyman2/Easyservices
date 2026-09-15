package com.easyservice.backend.service;

import com.easyservice.backend.infrastructure.FakeRandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EasyToolsServiceTest {

    private FakeRandomNumberGenerator randomNumberGenerator;
    private EasyToolsService easyToolsService;

    @BeforeEach
    void setUp() {
        randomNumberGenerator = new FakeRandomNumberGenerator();
        easyToolsService = new EasyToolsService(randomNumberGenerator);
    }

    @Test
    @DisplayName("Equal Split: 900 total divided by 3 people = 300 each")
    void calculateEqualSplit_Valid() {
        BigDecimal result = easyToolsService.calculateEqualSplit(BigDecimal.valueOf(900.00), 3);
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(result));
    }

    @Test
    @DisplayName("Equal Split BVA: Invalid count <= 0 throws IllegalArgumentException")
    void calculateEqualSplit_InvalidCount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                easyToolsService.calculateEqualSplit(BigDecimal.valueOf(100.00), 0));
    }

    @Test
    @DisplayName("Itemized Split: Aggregates item prices per user correctly")
    void calculateItemizedSplit_Valid() {
        Map<String, List<BigDecimal>> input = Map.of(
                "Alice", List.of(BigDecimal.valueOf(150.00), BigDecimal.valueOf(50.00)),
                "Bob", List.of(BigDecimal.valueOf(300.00))
        );

        Map<String, BigDecimal> result = easyToolsService.calculateItemizedSplit(input);
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(result.get("Alice")));
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(result.get("Bob")));
    }

    @Test
    @DisplayName("Random Payer Wheel: Uses FakeRandomNumberGenerator double for deterministic outcome selection")
    void selectRandomPayer_ControlledRandomness() {
        List<String> participants = List.of("Abebe", "Kebede", "Chala", "Tigist");

        // Force fake generator to return index 2 ("Chala")
        randomNumberGenerator.setFixedValue(2);
        String selected = easyToolsService.selectRandomPayer(participants);
        assertEquals("Chala", selected);

        // Force fake generator to return index 0 ("Abebe")
        randomNumberGenerator.setFixedValue(0);
        assertEquals("Abebe", easyToolsService.selectRandomPayer(participants));
    }
}
