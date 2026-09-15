package com.easyservice.backend.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantServiceTest {

    private final AssistantService assistant = new AssistantService("", "", "");

    @Test
    void respondsToEmotionalAndLeisureMessagesBeforeGenericFallback() {
        assertTrue(responseFor("hey im so tired").contains("That sounds exhausting"));
        assertTrue(responseFor("im tired").contains("That sounds exhausting"));
        assertTrue(responseFor("i wanna rest").contains("That sounds exhausting"));
        assertTrue(responseFor("i am so hyped like i just got off work and i wanna do sth what is there to do")
                .contains("You just got off work and want to do something fun"));
        String publicResponse = assistant.chat(
            List.of(Map.of("role", "user", "text", "find a coffee shop nearby")),
            List.of(Map.of(
                "id", "public-1",
                "title", "The Daily Grind",
                "sourceType", "PUBLIC_SOURCE",
                "sourceName", "Google Maps",
                "location", "123 Main Street",
                "website", "https://example.com",
                "availableQuantity", 0
            )),
            "en"
        ).text();
        assertTrue(publicResponse.contains("I found this from Google Maps"));
        assertTrue(publicResponse.contains("Although you cannot book it directly here"));
        assertTrue(publicResponse.contains("- Website: https://example.com"));
    }

    @Test
    void coversLocalConversationMarketplaceAndSupportRoutes() {
        List<Map<String, Object>> inventory = List.of(
                Map.of("id", "hotel-1", "title", "Bole Stay", "category", "HOTEL", "location", "Bole", "description", "Quiet room", "availableQuantity", 3),
                Map.of("id", "car-1", "title", "Addis Driver", "category", "CAR_RENTAL", "location", "Addis", "description", "Airport transfer", "availableQuantity", 2),
                Map.of("id", "event-1", "title", "Jazz Night", "category", "EVENT", "location", "Addis", "description", "Live music", "availableQuantity", 4),
                Map.of("id", "public-1", "title", "Daily Grind", "sourceType", "PUBLIC_SOURCE", "sourceName", "Google Maps", "location", "Bole", "website", "https://example.com", "availableQuantity", 0)
        );

        assertTrue(responseFor("hello").contains("I am doing well"));
        assertTrue(responseFor("whats up").contains("Not much"));
        assertTrue(responseFor("can u talk to me").contains("I'm here with you"));
        assertTrue(responseFor("what else").contains("keep talking"));
        assertEquals("EVENT", assistant.chat(messages("fun day"), inventory, "en").action());
        assertTrue(responseFor("from bole").contains("Bole"));
        assertTrue(responseFor("show public businesses", inventory).contains("I found this from Google Maps"));
        assertTrue(responseFor("i wanna eat", inventory).contains("I found this from Google Maps"));
        assertTrue(responseFor("i chose it").contains("Great choice"));
        assertEquals("HOTEL", assistant.chat(messages("find a hotel"), inventory, "en").action());
        assertEquals("CAR_RENTAL", assistant.chat(messages("find a car"), inventory, "en").action());
        assertEquals("EVENT", assistant.chat(messages("show events"), inventory, "en").action());
        assertEquals("help", assistant.chat(messages("help"), inventory, "en").action());
        assertEquals("help", assistant.chat(messages("cancel my booking"), inventory, "en").action());
        assertEquals("passport", assistant.chat(messages("open my profile"), inventory, "en").action());
        var providerSupport = assistant.chat(messages("provider support"), inventory, "en");
        assertEquals("provider-support", providerSupport.action(), providerSupport.text());
        assertEquals("become-provider", assistant.chat(messages("become a provider"), inventory, "en").action());
        assertEquals("help", assistant.chat(messages("how do I book"), inventory, "en").action());
        assertEquals("EVENT", assistant.chat(messages("find a fun day"), inventory, "en").action());
        assertEquals("hotel-1", assistant.chat(messages("recommend a hotel"), inventory, "en").listingId());
        assertTrue(!responseFor("something completely unrelated").isBlank());
    }

        @Test
        void coversAssistantLanguageInventoryAndFallbackBranches() {
        AssistantService configuredAssistant = new AssistantService(null, null, null);
        assertNotNull(configuredAssistant.chat(messages("hello"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("i am tired"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("what's up"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("talk to me"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("what else"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("i want food"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("from bole"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("fun day"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("i chose it"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("open my profile"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("provider support"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("become a provider"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("help"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("cancel my booking"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("how do I book"), List.of(), "am").text());
        assertNotNull(configuredAssistant.chat(messages("something unknown"), List.of(), "am").text());

        List<Map<String, Object>> inventory = List.of(
            Map.of("id", "event-1", "title", "Jazz Night", "category", "EVENT",
                "location", "Addis", "description", "Live music", "availableQuantity", "2"),
            Map.of("id", "event-2", "title", "Quiet Event", "category", "EVENT",
                "location", "Hawassa", "description", "Outdoor", "availableQuantity", 0),
            Map.of("id", "public-1", "title", "Unlisted Place", "sourceType", "PUBLIC_SOURCE",
                "sourceUrl", "https://source.example", "phone", "+251900000000", "price", "25",
                "availableQuantity", "not-a-number"),
            Map.of("id", "public-2", "title", "Empty Details", "sourceType", "PUBLIC_SOURCE",
                "availableQuantity", 0)
        );

        assertEquals("EVENT", assistant.chat(messages("show events"), inventory, "en").action());
        assertEquals("EVENT", assistant.chat(messages("find an event in Lalibela"), inventory, "en").action());
        assertTrue(assistant.chat(messages("find a cozy place"), inventory, "en").text().contains("Jazz Night"));
        assertTrue(assistant.chat(messages("find a public source"), inventory, "en").text().contains("source.example"));
        assertTrue(assistant.chat(messages("find a restaurant"), List.of(
            Map.of("id", "empty", "title", "Empty Details", "sourceType", "PUBLIC_SOURCE",
                "availableQuantity", 0)), "en").text().contains("not publicly listed"));
        assertTrue(assistant.chat(List.of(Map.of("role", "assistant", "text", "old")), inventory, "en")
            .text().contains("I can help"));
        assertDoesNotThrow(() -> new AssistantService("http://localhost:1", "key", "model")
            .chat(List.of(Map.of("role", "user", "text", "hello"),
                Map.of("role", "system", "text", "ignored"),
                Map.of("role", "assistant", "text", "hi"),
                Map.of("role", "user", "text", "")), inventory, "en"));
        }

    private String responseFor(String text) {
        return responseFor(text, List.of());
    }

    private String responseFor(String text, List<Map<String, Object>> inventory) {
        return assistant.chat(
                messages(text),
                inventory,
                "en"
        ).text();
    }

    private List<Map<String, Object>> messages(String text) {
        return List.of(Map.of("role", "user", "text", text));
    }
}