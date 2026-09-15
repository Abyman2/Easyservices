package com.easyservice.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AssistantService {

    private final RestClient restClient;
    private final String aiBaseUrl;
    private final String aiApiKey;
    private final String aiModel;

    public AssistantService(
            @Value("${easyservice.ai.base-url:}") String aiBaseUrl,
            @Value("${easyservice.ai.api-key:}") String aiApiKey,
            @Value("${easyservice.ai.model:llama-3.1-8b-instant}") String aiModel) {
        this.aiBaseUrl = aiBaseUrl == null ? "" : aiBaseUrl.trim();
        this.aiApiKey = aiApiKey == null ? "" : aiApiKey.trim();
        this.aiModel = aiModel;
        this.restClient = RestClient.builder().build();
    }

    public ChatResponse chat(List<Map<String, Object>> messages, List<Map<String, Object>> inventory, String language) {
        if (hasAiProvider()) {
            try {
                return callAiProvider(messages, inventory, language);
            } catch (RuntimeException ignored) {
                // The local response keeps the demo usable when the free-tier service is unavailable.
            }
        }
        return localResponse(messages, inventory, language);
    }

    private boolean hasAiProvider() {
        return !aiBaseUrl.isBlank() && !aiApiKey.isBlank();
    }

    private ChatResponse callAiProvider(List<Map<String, Object>> messages, List<Map<String, Object>> inventory, String language) {
        List<Map<String, String>> promptMessages = new ArrayList<>();
        promptMessages.add(Map.of(
                "role", "system",
                "content", systemPrompt(language, inventory)
        ));
        for (Map<String, Object> message : messages) {
            Object role = message.get("role");
            Object text = message.get("text");
            if (role != null && text != null && ("user".equals(role) || "assistant".equals(role))) {
                promptMessages.add(Map.of("role", role.toString(), "content", text.toString()));
            }
        }

        Map<String, Object> request = new HashMap<>();
        request.put("model", aiModel);
        request.put("temperature", 0.2);
        request.put("messages", promptMessages);

        JsonNode response = restClient.post()
                .uri(aiBaseUrl.endsWith("/chat/completions") ? aiBaseUrl : aiBaseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + aiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JsonNode.class);

        String text = response == null ? "" : response.at("/choices/0/message/content").asText("");
        if (text.isBlank()) {
            throw new IllegalStateException("AI provider returned no assistant message");
        }
        ChatResponse platformContext = localResponse(messages, inventory, language);
        return new ChatResponse(text, platformContext.action(), platformContext.listingId());
    }

    private String systemPrompt(String language, List<Map<String, Object>> inventory) {
        return "You are Easy Assistant for EasyService, an Ethiopian marketplace for hotels, cars, events and products. "
                + "Answer naturally and briefly. Use only the inventory context below for availability, prices and names. "
                + "Never claim to have completed a booking or payment. Guide the user to the existing marketplace or support flow. "
                + "Reply in " + ("am".equalsIgnoreCase(language) ? "Amharic" : "English") + ".\nInventory:\n" + inventory;
    }

    private ChatResponse localResponse(List<Map<String, Object>> messages, List<Map<String, Object>> inventory, String language) {
        boolean amharic = "am".equalsIgnoreCase(language);
        String query = lastUserMessage(messages).toLowerCase(Locale.ROOT);
        String action = null;
        String listingId = null;

        if (containsAny(query, "profile", "passport", "my account", "መገለጫ")) {
            return new ChatResponse(amharic ? "የመገለጫ ገጽዎን እከፍታለሁ።" : "I will open your profile and booking activity.", "passport", null);
        }
        if (containsAny(query, "provider support", "provider help", "አቅራቢ ድጋፍ")) {
            return new ChatResponse(amharic ? "የአቅራቢ ድጋፍን እከፍታለሁ።" : "I will open provider support.", "provider-support", null);
        }
        if (containsAny(query, "help", "support", "booking help", "contact", "ድጋፍ", "እርዳታ")) {
            return new ChatResponse(amharic ? "የደንበኛ ድጋፍን እከፍታለሁ።" : "I will open customer support with booking and policy answers.", "help", null);
        }
        if (containsAny(query, "cancel", "cancellation", "refund", "ስረዛ")) {
            return new ChatResponse(amharic ? "ስለ ስረዛ ፖሊሲ እና ድጋፍ እንዲያዩ የድጋፍ ገጹን እከፍታለሁ።" : "Cancellation depends on the listing policy. I will open support so you can review it.", "help", null);
        }

        String category = null;
        if (containsAny(query, "hotel", "stay", "room", "resort", "lodge", "ሆቴል", "መኖሪያ")) category = "HOTEL";
        if (containsAny(query, "car", "cars", "drive", "rental", "vehicle", "መኪና")) category = "CAR_RENTAL";
        if (containsAny(query, "event", "events", "entertainment", "weekend", "concert", "festival", "experience", "ዝግጅት", "ልምድ")) category = "EVENT";

        if (category != null) {
            Map<String, Object> match = firstAvailable(inventory, category, query);
            if (containsAny(query, "pick", "choose", "recommend", "one yourself", "surprise", "ምረጥ", "ምከረኝ") && match != null) {
                listingId = stringValue(match.get("id"));
                String title = stringValue(match.get("title"));
                return new ChatResponse(amharic ? title + "ን እመክራለሁ። ዝርዝሩን ይክፈቱ።" : "I recommend " + title + ". Open the listing to see details and available options.", null, listingId);
            }
            action = category;
            return new ChatResponse(amharic ? "አማራጮቹን እያሳየሁ ነው።" : "I will show the matching EasyService inventory.", action, null);
        }

        return new ChatResponse(amharic ? "ሆቴሎችን፣ መኪናዎችን፣ ዝግጅቶችን፣ ቦታ ማስያዣዎችን ወይም ድጋፍን ልረዳዎት እችላለሁ።" : "I can help with hotels, cars, events, bookings, cancellations, profiles, and support. Tell me what you need and I will take you there.", null, null);
    }

    private Map<String, Object> firstAvailable(List<Map<String, Object>> inventory, String category, String query) {
        for (Map<String, Object> listing : inventory) {
            if (category.equalsIgnoreCase(stringValue(listing.get("category")))
                    && numberValue(listing.get("availableQuantity")) > 0
                    && textMatches(listing, query)) {
                return listing;
            }
        }
        for (Map<String, Object> listing : inventory) {
            if (category.equalsIgnoreCase(stringValue(listing.get("category")))
                    && numberValue(listing.get("availableQuantity")) > 0) {
                return listing;
            }
        }
        return null;
    }

    private boolean textMatches(Map<String, Object> listing, String query) {
        String searchable = (stringValue(listing.get("title")) + " " + stringValue(listing.get("location")) + " " + stringValue(listing.get("description"))).toLowerCase(Locale.ROOT);
        return !containsAny(query, "bole", "airport", "addis", "bishoftu", "hawassa", "lalibela", "bahir") || searchable.contains("bole") || searchable.contains("airport") || searchable.contains("addis");
    }

    private String lastUserMessage(List<Map<String, Object>> messages) {
        for (int index = messages.size() - 1; index >= 0; index--) {
            Map<String, Object> message = messages.get(index);
            if ("user".equals(message.get("role"))) return stringValue(message.get("text"));
        }
        return "";
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) if (text.contains(term)) return true;
        return false;
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }

    private int numberValue(Object value) {
        if (value instanceof Number number) return number.intValue();
        try { return Integer.parseInt(stringValue(value)); } catch (NumberFormatException ignored) { return 0; }
    }

    public record ChatResponse(String text, String action, String listingId) {}
}
