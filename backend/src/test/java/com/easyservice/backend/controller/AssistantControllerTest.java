package com.easyservice.backend.controller;

import com.easyservice.backend.service.AssistantService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssistantControllerTest {

    @Test
    void acceptsNullCollectionsAndReturnsAssistantResponse() {
        AssistantController controller = new AssistantController(new AssistantService("", "", ""));

        var response = controller.chat(new AssistantController.ChatRequest(null, null, "en"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("I can help with hotels, cars, events, bookings, cancellations, profiles, and support. Tell me what you need and I will take you there.", response.getBody().text());
    }

    @Test
    void forwardsMessagesInventoryAndLanguage() {
        AssistantController controller = new AssistantController(new AssistantService("", "", ""));

        var response = controller.chat(new AssistantController.ChatRequest(
                List.of(Map.of("role", "user", "text", "whats up")),
                List.of(),
                "en"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Not much, I'm here and ready to help. We can find food, a quiet place, or something fun for tonight. What are you in the mood for?", response.getBody().text());
    }
}