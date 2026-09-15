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
                + "Have a natural, friendly conversation. Answer greetings and casual questions warmly, then offer useful EasyService help. "
            + "When someone says they are tired, exhausted, stressed, overwhelmed, or want to rest, acknowledge how they feel first; do not reply with a greeting or a category menu. Offer a gentle choice such as finding a quiet stay or relaxing place, or simply continuing the conversation. Ask for their city only when it would help. "
                + "For requests like a fun day, build a practical plan using events, experiences, stays, cars, shops, and public discoveries. "
                + "Use only the inventory context below for availability, prices and names. "
                + "CRITICAL RULE FOR EXTERNAL SEARCHES: Inventory entries marked sourceType PUBLIC_SOURCE and bookable false are external discoveries. When presenting one, begin with 'I found this from [Source Name]. Although you cannot book it directly here, you can still get their information below:' or a close natural variation. Then use a clean bulleted list of only the available source details, such as address, website, phone and price. Clearly say when price was not publicly listed. Never imply that an external result is bookable through EasyService. "
                + "Never claim to have completed a booking or payment. Guide the user to the existing marketplace or support flow. "
                + "Reply in " + ("am".equalsIgnoreCase(language) ? "Amharic" : "English") + ".\nInventory:\n" + inventory;
    }

    private ChatResponse localResponse(List<Map<String, Object>> messages, List<Map<String, Object>> inventory, String language) {
        boolean amharic = "am".equalsIgnoreCase(language);
        String query = lastUserMessage(messages).toLowerCase(Locale.ROOT);
        String action = null;
        String listingId = null;

        if (containsAny(query, "tired", "exhausted", "worn out", "need a rest", "need rest", "want to rest", "wanna rest", "sleepy", "burned out", "burnt out", "overwhelmed", "ደክሞኛል", "እረፍት")) {
            return new ChatResponse(amharic
                ? "የደከመዎት ይመስላል። ትንሽ እረፍት ይገባዎታል። ጸጥ ያለ ሆቴል ወይም የሚያረጋጋ ቦታ እንድፈልግልዎ ይፈልጋሉ፣ ወይስ ትንሽ መወያየት ይመርጣሉ?"
                : "That sounds exhausting. You deserve a real break. Would you like me to find a quiet stay or a relaxing place nearby, or would you rather just talk for a moment? Tell me your city if you want me to narrow it down.", null, null);
        }

        if (containsAny(query, "how are you", "how r u", "hello", "hi", "hey", "good morning", "good afternoon", "good evening", "ሰላም", "እንዴት ነህ")) {
            return new ChatResponse(amharic
                ? "ደህና ነኝ፣ እናመሰግናለሁ! ለጉዞዎ ሆቴል፣ መኪና፣ ዝግጅት ወይም የአካባቢ ምንጭ መረጃ ልፈልግልዎ እችላለሁ።"
                : "I am doing well, thank you. I am ready to help plan your EasyService day. Are you looking for a stay, a car, an event, or something local to discover?", null, null);
        }

            if (containsAny(query, "what's up", "whats up", "how is it going", "how's it going") || "sup".equals(query)) {
                return new ChatResponse(amharic
                    ? "ሁሉም ጥሩ ነው። ለዛሬ ቀንዎ ምግብ፣ ጸጥ ያለ ቦታ ወይም የምሽት ዝግጅት ማግኘት እችላለሁ። እርስዎ ምን ስሜት ላይ ነዎት?"
                    : "Not much, I'm here and ready to help. We can find food, a quiet place, or something fun for tonight. What are you in the mood for?", null, null);
            }

            if (containsAny(query, "can u talk", "can you talk", "talk to me", "just talk", "chat with me", "keep me company", "lonely", "are you there")) {
                return new ChatResponse(amharic
                    ? "እሺ፣ እዚህ ነኝ። ስለ ቀንዎ መነጋገር ወይም ለዛሬ ምሽት እቅድ ማውጣት እንችላለን። አሁን ምን እያሰቡ ነው?"
                    : "Of course. I'm here with you. We can just talk, or we can figure out something nice for the rest of your day. What's on your mind?", null, null);
            }

            if (containsAny(query, "what else", "anything else", "what more", "and then")) {
                return new ChatResponse(amharic
                    ? "ከእኔ ጋር መነጋገር፣ ምግብ መፈለግ፣ ጸጥ ያለ ቦታ ማግኘት ወይም የምሽት ዝግጅት ማቀድ እንችላለን። አሁን የትኛው ይስማማዎታል?"
                    : "We can keep talking, find something to eat, look for a quiet place, or plan an evening event. What sounds best right now?", null, null);
            }

            if (containsAny(query, "eat", "food", "hungry", "meal", "dinner", "lunch", "breakfast", "restaurant", "coffee shop", "cafe")) {
                Map<String, Object> publicMatch = firstPublicSource(inventory);
                if (publicMatch != null) {
                return new ChatResponse("I found this from " + sourceName(publicMatch) + ". Although you cannot book it directly here, you can still get their information below:\n" + publicDetails(publicMatch), null, stringValue(publicMatch.get("id")));
                }
                return new ChatResponse(amharic
                    ? "ምን መብላት እንደሚፈልጉ ልፈልግልዎ እችላለሁ። የትኛው ከተማ ነዎት፣ እና ምግብ ቤት፣ ካፌ ወይስ ፈጣን ምግብ ይፈልጋሉ?"
                    : "Absolutely. I can help you find somewhere to eat. What city are you in, and are you in the mood for a restaurant, a cafe, or something quick?", null, null);
            }

                if (containsAny(query, "from bole", "in bole", "near bole", "around bole", "from addis", "in addis")) {
                    return new ChatResponse(amharic
                        ? "ቦሌ አካባቢ እንዳሉ ገባኝ። ከቦሌ አካባቢ ሆቴል፣ መኪና፣ ምግብ ቤት ወይም ዝግጅት እንድፈልግ ይፈልጋሉ?"
                        : "Got it, you're around Bole. I can narrow the options to Bole for a stay, car, restaurant, or event. Which one should I look for?", null, null);
                }

        if (containsAny(query, "fun day", "have fun", "something fun", "what can i do", "what is there to do", "what's there to do", "something to do", "things to do", "where should i go", "just got off work", "after work", "hyped", "excited", "bored", "do sth", "do something", "tonight", "this evening", "ደስ የሚል", "መዝናናት")) {
            return new ChatResponse(amharic
                ? "ለመዝናናት ዝግጅቶችን፣ ልምዶችን እና ከህዝብ ምንጮች የተገኙ ቦታዎችን እፈልግልዎታለሁ።"
            : "You just got off work and want to do something fun, got it. I can help you choose an event, a relaxed place to hang out, or a local discovery. What city are you in, and are you feeling social or more laid-back?", "EVENT", null);
        }

        if (containsAny(query, "public source", "discovered", "nearby business", "restaurant", "coffee shop", "cafe", "shopping", "souvenir", "public", "ህዝብ ምንጭ")) {
            Map<String, Object> publicMatch = firstPublicSource(inventory);
            if (publicMatch != null) {
            return new ChatResponse(amharic
                ? stringValue(publicMatch.get("title")) + " ከ" + sourceName(publicMatch) + " ተገኝቷል። በቀጥታ በEasyService ማስያዝ አይችሉም፣ ነገር ግን መረጃውን ከታች ማግኘት ይችላሉ።\n" + publicDetails(publicMatch)
                : "I found this from " + sourceName(publicMatch) + ". Although you cannot book it directly here, you can still get their information below:\n" + publicDetails(publicMatch), null, stringValue(publicMatch.get("id")));
            }
        }

        if (containsAny(query, "chill", "cozy", "cosy", "quiet", "relaxing", "relaxed", "evening", "date night", "night out", "spot", "place to unwind", "lounge", "cafe", "restaurant")) {
            Map<String, Object> eventMatch = firstAvailable(inventory, "EVENT", query);
            Map<String, Object> publicMatch = firstPublicSource(inventory);
            if (eventMatch != null) {
                return new ChatResponse(amharic
                        ? stringValue(eventMatch.get("title")) + " ለምቾት ምሽት ጥሩ ምርጫ ይመስላል። ዝርዝሩን ይክፈቱ እና ቀኑን ይምረጡ።"
                        : "For a cozy evening, " + stringValue(eventMatch.get("title")) + " looks like a good place to start. Open it to check the details and date.", "EVENT", stringValue(eventMatch.get("id")));
            }
            if (publicMatch != null) {
                return new ChatResponse(amharic
                        ? stringValue(publicMatch.get("title")) + " ለምቾት ምሽት ሊስማማ ይችላል። ይህ የህዝብ ምንጭ ግኝት ነው እና በEasyService አይያዝም።"
                        : "I found this from " + sourceName(publicMatch) + ". Although you cannot book it directly here, you can still get their information below:\n" + publicDetails(publicMatch), null, stringValue(publicMatch.get("id")));
            }
            return new ChatResponse(amharic
                    ? "ምቾት ያለው ምሽት ጥሩ ሀሳብ ነው። የምሽት ዝግጅት፣ ጸጥ ያለ ቦታ ወይም ካፌ ይፈልጋሉ? ከተማዎንም ይንገሩኝ።"
                    : "A cozy evening sounds lovely. Would you prefer an evening event, a quiet spot, or a cafe? Tell me your city and I’ll narrow it down.", "EVENT", null);
        }

        if (containsAny(query, "public source", "discovered", "nearby business", "restaurant", "shopping", "souvenir", "public", "ህዝብ ምንጭ")) {
            Map<String, Object> publicMatch = firstPublicSource(inventory);
            if (publicMatch != null) {
                String details = publicDetails(publicMatch);
                return new ChatResponse(amharic
                        ? stringValue(publicMatch.get("title")) + " ከህዝብ ምንጭ ተገኝቷል። በEasyService ሊያዝ አይችልም። " + details
                        : "I found this from " + sourceName(publicMatch) + ". Although you cannot book it directly here, you can still get their information below:\n" + details, null, stringValue(publicMatch.get("id")));
            }
        }

        if (containsAny(query, "chose", "choose", "picked", "selected", "i chose", "i picked")) {
            return new ChatResponse(amharic
                    ? "ጥሩ ምርጫ ነው። ለቀሪው ጉዞዎ መኪና፣ ዝግጅት እና የኢትዮጵያ ስጦታ እንዲያገኙ ልረዳዎት እችላለሁ። ከታች ያሉትን ምድቦች ይምረጡ።"
                    : "Great choice. I can now help with the rest of your trip: a driver, weekend events, and an Ethiopian gift to take home. Choose a category below.", null, null);
        }

        boolean wantsCar = containsAny(query, "car", "driver", "drive", "rental", "vehicle", "መኪና");
        boolean wantsHotel = containsAny(query, "hotel", "stay", "room", "resort", "lodge", "weekend", "ሆቴል", "መኖሪያ");
        boolean wantsEvent = containsAny(query, "event", "events", "entertainment", "weekend", "concert", "festival", "where to go", "things to do", "ዝግጅት", "ልምድ");
        boolean wantsStore = containsAny(query, "take", "home country", "gift", "souvenir", "shop", "shopping", "product", "ስጦታ", "ግዢ");

        if ((wantsCar ? 1 : 0) + (wantsHotel ? 1 : 0) + (wantsEvent ? 1 : 0) + (wantsStore ? 1 : 0) >= 2) {
            return new ChatResponse(amharic
                    ? "ለሳምንቱ መጨረሻ መኖሪያ፣ ሹፌር/መኪና፣ ዝግጅቶች እና ወደ ቤት የሚወስዱ ስጦታዎችን ልፈልግልዎ እችላለሁ። የትኛውን መጀመሪያ እንይ?"
                    : "I can help plan the whole visit: a weekend stay, a car with a driver, things to do around your meeting, and an Ethiopian gift to take home. Which should we start with?", null, null);
        }

        if (containsAny(query, "profile", "passport", "my account", "መገለጫ")) {
            return new ChatResponse(amharic ? "የመገለጫ ገጽዎን እከፍታለሁ።" : "I will open your profile and booking activity.", "passport", null);
        }
        if (containsAny(query, "provider support", "provider help", "አቅራቢ ድጋፍ")) {
            return new ChatResponse(amharic ? "የአቅራቢ ድጋፍን እከፍታለሁ።" : "I will open provider support.", "provider-support", null);
        }
        if (containsAny(query, "provider", "list a service", "become", "አቅራቢ")) {
            return new ChatResponse(amharic ? "የአቅራቢ መረጃን እና መመዝገቢያን እከፍታለሁ።" : "I will open the provider onboarding and listing workspace.", "become-provider", null);
        }
        if (containsAny(query, "help", "support", "booking help", "contact", "ድጋፍ", "እርዳታ")) {
            return new ChatResponse(amharic ? "የደንበኛ ድጋፍን እከፍታለሁ።" : "I will open customer support with booking and policy answers.", "help", null);
        }
        if (containsAny(query, "cancel", "cancellation", "refund", "ስረዛ")) {
            return new ChatResponse(amharic ? "ስለ ስረዛ ፖሊሲ እና ድጋፍ እንዲያዩ የድጋፍ ገጹን እከፍታለሁ።" : "Cancellation depends on the listing policy. I will open support so you can review it.", "help", null);
        }
        if (containsAny(query, "book", "booking", "reserve", "how does easyservice work", "how do i book", "ቦታ ማስያዣ", "እንዴት")) {
            return new ChatResponse(amharic ? "የቦታ ማስያዣ እርምጃዎችን እና የክፍያ ሂደቱን በድጋፍ ገጽ ይመልከቱ።" : "Choose a verified listing, select its option and dates, enter your details, then review and pay securely. I will open the help page for the full guide.", "help", null);
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

    private Map<String, Object> firstPublicSource(List<Map<String, Object>> inventory) {
        for (Map<String, Object> listing : inventory) {
            if ("PUBLIC_SOURCE".equalsIgnoreCase(stringValue(listing.get("sourceType")))) return listing;
        }
        return null;
    }

    private String publicDetails(Map<String, Object> listing) {
        List<String> details = new ArrayList<>();
        String location = stringValue(listing.get("location"));
        String website = stringValue(listing.get("website"));
        if (website.isBlank()) website = stringValue(listing.get("sourceUrl"));
        String phone = stringValue(listing.get("phone"));
        Object price = listing.get("price");
        if (!location.isBlank()) details.add("- Address/location: " + location);
        if (!website.isBlank()) details.add("- Website: " + website);
        if (!phone.isBlank()) details.add("- Phone: " + phone);
        if (price != null && !price.toString().isBlank() && !"0".equals(price.toString())) details.add("- Published price: " + price);
        if (details.isEmpty()) details.add("- Price and contact details were not publicly listed; check the source website or call the business.");
        return String.join("\n", details);
    }

    private String sourceName(Map<String, Object> listing) {
        String source = stringValue(listing.get("sourceName"));
        return source.isBlank() ? "a public source" : source;
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
