package com.easyservice.discovery.service;

import com.easyservice.discovery.model.*;
import com.easyservice.discovery.source.PublicSourceCrawler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class AddisDiscoveryService {
    private final boolean testMode;
    private final int testLimitPerCategory;
    private final boolean liveEnabled;
    private final PublicSourceCrawler crawler;
    private final List<DiscoverySource> sources;
    private final Executor crawlExecutor;

    public AddisDiscoveryService(
            @Value("${easyservice.discovery.test-mode:true}") boolean testMode,
            @Value("${easyservice.discovery.test-limit-per-category:5}") int testLimitPerCategory,
            @Value("${easyservice.discovery.live-enabled:true}") boolean liveEnabled,
            PublicSourceCrawler crawler,
            ObjectMapper objectMapper) {
        this.testMode = testMode;
        this.testLimitPerCategory = Math.max(1, testLimitPerCategory);
        this.liveEnabled = liveEnabled;
        this.crawler = crawler;
        this.sources = loadSources(objectMapper);
        this.crawlExecutor = Executors.newFixedThreadPool(
            Math.max(1, Math.min(8, this.sources.size())));
    }

    public List<MarketplaceListing> discover(LocationQuery query) {
        String city = query.city().trim();
        String area = query.area().trim();

        List<MarketplaceListing> results = new ArrayList<>();

        // Real public-source discovery comes first.
        if (liveEnabled) {
            List<CompletableFuture<List<MarketplaceListing>>> crawls = sources.stream()
                    .filter(source -> source.enabled() && source.category() == query.category())
                    .map(source -> CompletableFuture.supplyAsync(
                            () -> crawler.crawl(source, city, area), crawlExecutor))
                    .toList();

            for (CompletableFuture<List<MarketplaceListing>> crawl : crawls) {
                try {
                    results.addAll(crawl.get());
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                    // A slow or blocked source must not hide other discovery results.
                }
            }
        }

        // Keep the verified Phase-3 catalog as a deterministic fallback.
        // This makes development resilient when a public source blocks a crawler.
        results.addAll(catalog().stream()
                .filter(l -> l.category() == query.category())
                .filter(l -> normalize(l.city()).equals(normalize(city)))
                .filter(l -> "ALL".equalsIgnoreCase(area) || "*".equals(area)
                        || normalize(l.area()).equals(normalize(area)))
                .toList());

        results = dedupe(results);
        results.sort(Comparator.comparing(MarketplaceListing::name, String.CASE_INSENSITIVE_ORDER));

        if (testMode) return results.stream().limit(testLimitPerCategory).toList();
        return results;
    }

    public List<DiscoverySource> getSources() {
        return List.copyOf(sources);
    }

    private List<MarketplaceListing> dedupe(List<MarketplaceListing> input) {
        List<MarketplaceListing> unique = new ArrayList<>();

        for (MarketplaceListing candidate : input) {
            int matchIndex = -1;

            for (int i = 0; i < unique.size(); i++) {
                if (sameBusiness(unique.get(i), candidate)) {
                    matchIndex = i;
                    break;
                }
            }

            if (matchIndex < 0) {
                unique.add(candidate);
            } else {
                unique.set(matchIndex,
                        mergeListings(unique.get(matchIndex), candidate));
            }
        }

        return unique;
    }

    private boolean sameBusiness(MarketplaceListing a, MarketplaceListing b) {
        if (a == null || b == null) return false;
        if (a.category() != b.category()) return false;

        String aDomain = normalizeDomain(a.website());
        String bDomain = normalizeDomain(b.website());

        if (!aDomain.isBlank() && aDomain.equals(bDomain)) {
            return true;
        }

        String aPhone = normalizePhone(a.phone());
        String bPhone = normalizePhone(b.phone());

        if (!aPhone.isBlank() && aPhone.equals(bPhone)) {
            return true;
        }

        String aName = normalize(a.name());
        String bName = normalize(b.name());

        return !aName.isBlank() && aName.equals(bName);
    }

    private MarketplaceListing mergeListings(
            MarketplaceListing first,
            MarketplaceListing second) {

        MarketplaceListing preferred;

        if (!isLive(first)) {
            preferred = first;
        } else {
            preferred = second;
        }

        MarketplaceListing other =
                preferred == first ? second : first;

        return new MarketplaceListing(
                preferred.id(),
                firstNonBlank(preferred.name(), other.name()),
                preferred.category(),
                firstNonBlank(preferred.country(), other.country()),
                firstNonBlank(preferred.city(), other.city()),
                firstNonBlank(preferred.area(), other.area()),
                firstNonBlank(preferred.address(), other.address()),
                firstNonBlank(preferred.description(), other.description()),
                firstNonBlank(preferred.website(), other.website()),
                firstNonBlank(preferred.phone(), other.phone()),
                firstNonBlank(preferred.imageUrl(), other.imageUrl()),
                firstNonBlank(preferred.price(), other.price()),
                firstNonBlank(preferred.currency(), other.currency()),
                firstNonBlank(preferred.priceUnit(), other.priceUnit()),
                choosePriceSource(preferred, other),
                preferred.sourceType(),
                firstNonBlank(preferred.sourceName(), other.sourceName()),
                firstNonBlank(preferred.sourceUrl(), other.sourceUrl()),
                preferred.lastChecked() != null
                        ? preferred.lastChecked()
                        : other.lastChecked(),
                mergeTags(preferred.tags(), other.tags()),
                mergeImages(preferred.imageUrls(), other.imageUrls())
        );
    }

    private List<String> mergeImages(
            List<String> first,
            List<String> second) {

        Set<String> images = new LinkedHashSet<>();

        if (first != null) {
            images.addAll(first);
        }

        if (second != null) {
            images.addAll(second);
        }

        return new ArrayList<>(images);
    }

    private boolean isLive(MarketplaceListing listing) {
        return listing != null
                && listing.id() != null
                && listing.id().startsWith("live_");
    }

    private String choosePriceSource(
            MarketplaceListing a,
            MarketplaceListing b) {

        String price = firstNonBlank(a.price(), b.price());

        if (price == null) {
            return "NOT_PUBLISHED";
        }

        if ("PUBLISHED_SOURCE".equals(a.priceSource())
                || "PUBLISHED_SOURCE".equals(b.priceSource())) {
            return "PUBLISHED_SOURCE";
        }

        return firstNonBlank(a.priceSource(), b.priceSource());
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank()
                ? first
                : second;
    }

    private List<String> mergeTags(
            List<String> first,
            List<String> second) {

        Set<String> tags = new LinkedHashSet<>();

        if (first != null) {
            tags.addAll(first);
        }

        if (second != null) {
            tags.addAll(second);
        }

        return new ArrayList<>(tags);
    }

    private String normalizePhone(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("\\D", "");
    }

    private String normalizeDomain(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String domain = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceFirst("^https?://", "")
                .replaceFirst("^www\\.", "");

        int slash = domain.indexOf('/');

        if (slash >= 0) {
            domain = domain.substring(0, slash);
        }

        int query = domain.indexOf('?');

        if (query >= 0) {
            domain = domain.substring(0, query);
        }

        return domain;
    }

    private List<DiscoverySource> loadSources(ObjectMapper mapper) {
        try {
            Map<String,Object> root = mapper.readValue(
                    new ClassPathResource("addis-discovery-sources.json").getInputStream(),
                    new TypeReference<>() {});
            Object raw = root.get("liveSources");
            if (raw == null) return defaultSources();
            return mapper.convertValue(raw, new TypeReference<>() {});
        } catch (Exception e) {
            return defaultSources();
        }
    }

    private List<DiscoverySource> defaultSources() {
        return List.of(
                new DiscoverySource("Bole Green Hotel", ListingCategory.HOTEL,
                        List.of("https://bolegreen.com/"), true),
                new DiscoverySource("Addis Car Rental", ListingCategory.CAR,
                        List.of("https://www.addiscarrent.com/car-rental-in-addis-ababa/"), true),
                new DiscoverySource("Eventful Ethiopia", ListingCategory.EVENT,
                        List.of("https://eventful.et/"), true),
                new DiscoverySource("Meron Addis Ababa", ListingCategory.STORE,
                        List.of("https://meronaddisababa.com/contact-us/"), true)
        );
    }

    private String normalize(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    // ---------------- verified Phase-3 fallback catalog ----------------

    private List<MarketplaceListing> catalog() {
        return List.of(
                hotel("hotel_bole_green", "Bole Green Hotel", "Bole",
                        "Bole, Addis Ababa, Ethiopia", "Hotel in Addis Ababa.",
                        "https://bolegreen.com/", "+251 947 128 080",
                        "https://bolegreen.com/", List.of("Bole", "hotel")),
                hotel("hotel_jupiter_bole", "Jupiter International Hotel Bole", "Bole",
                        "African Avenue, Bole, Addis Ababa, Ethiopia",
                        "Hotel near Addis Ababa International Airport.",
                        "https://www.jupiterinternationalhotel.com/", "+251 11 552 7333",
                        "https://www.jupiterinternationalhotel.com/", List.of("Bole", "hotel")),
                hotel("hotel_mosaic_bole", "The Mosaic Hotel", "Bole",
                        "Bole Road, near Medhanealem Church and Edna Mall, Addis Ababa, Ethiopia",
                        "Hotel in Bole with guest rooms, restaurant and accommodation services.",
                        "https://themosaichotel.co/", "+251 911 208 717",
                        "https://themosaichotel.co/", List.of("Bole", "hotel")),
                hotel("hotel_sabon_bole", "Sabon Hotel", "Bole",
                        "Namibia Street, Bole, Addis Ababa, Ethiopia",
                        "Hotel in Bole offering accommodation and restaurant services.",
                        "https://sabonhotel.com/", "+251 11 639 3535",
                        "https://sabonhotel.com/", List.of("Bole", "hotel")),
                hotel("hotel_cfun_bole", "C Fun Addis Hotel", "Bole",
                        "Bole Sub-city, Wereda 4, House #167, Mickey Leland St., Addis Ababa, Ethiopia",
                        "Boutique hotel in Bole with rooms, dining and guest services.",
                        "https://www.cfunaddishotel.com/", "+251 930 337 133",
                        "https://www.cfunaddishotel.com/", List.of("Bole", "hotel")),

                car("car_addis_rental_22", "Addis Car Rental", "22 Mazoria",
                        "Imperial, 22 Mazoria Road, Addis Ababa, Ethiopia",
                        "Car rental service offering daily, weekly and monthly rentals.",
                        "https://www.addiscarrent.com/", "+251 92 142 1040",
                        "https://www.addiscarrent.com/car-rental-in-addis-ababa/",
                        "$60/day", "USD", "per day", List.of("22 Mazoria", "car rental")),
                car("car_ethio_rental_22", "Ethio Car Rental & Tours", "22 Mazoria",
                        "22, in front of Golagul, Town Square Mall, Addis Ababa, Ethiopia",
                        "Car rental and tours service in Addis Ababa.",
                        "https://ethiocarrent.com/", "+251 906 240 000",
                        "https://ethiocarrent.com/contact-us/", null, "ETB", null,
                        List.of("22 Mazoria", "car rental")),
                car("car_platinum_bole", "Platinum Car Rental PLC", "Bole",
                        "Welelay Building, 5th Floor, near Adey Abeba International Stadium, Bole, Addis Ababa, Ethiopia",
                        "Chauffeur-driven car hire across Addis Ababa.",
                        "https://ethiopia-car-rental.com/", "+251 978 848 550",
                        "https://ethiopia-car-rental.com/locations/addis-ababa/", null, "ETB", null,
                        List.of("Bole", "car rental", "chauffeur")),
                car("car_jano_bole", "Jano Car Rental Addis", "Bole",
                        "Bole, Addis Ababa, Ethiopia", "Premium car rental and chauffeur services based in Bole.",
                        "https://www.janocarental.com/", "+251 911 269 582",
                        "https://www.janocarental.com/", null, "ETB", null, List.of("Bole", "car rental")),
                car("car_zerihun_bole", "Ethio Zerihun Car Rental", "Bole",
                        "Bole Medhanialem, Addis Ababa, Ethiopia",
                        "Car rental service offering vehicles with drivers in Addis Ababa.",
                        "https://zerihuncarrent.com/", "+251 92 343 3967",
                        "https://zerihuncarrent.com/", null, "ETB", null, List.of("Bole", "car rental")),

                store("store_cktech_bole", "CK Tech", "Bole",
                        "Bole Medanialem Helzer Tower, 1st Floor 110A, Addis Ababa, Ethiopia",
                        "Electronics and technology shop in Bole.",
                        "https://www.cktech.store/", "+251 11 246 296",
                        "https://www.cktech.store/", List.of("Bole", "electronics", "store")),
                store("store_meron_bole", "Meron Addis Ababa", "Bole",
                        "Reality Plaza, Ground Floor, Bole, Addis Ababa, Ethiopia",
                        "Ethiopian leather and fashion store.", "https://meronaddisababa.com/", "+251 933 223344",
                        "https://meronaddisababa.com/contact-us/", List.of("Bole", "store", "leather")),
                store("store_meron_kazanchis", "Meron Addis Ababa - Kazanchis", "Kazanchis",
                        "Ayat Building, Kazanchis, Addis Ababa, Ethiopia",
                        "Meron Addis Ababa store location in Kazanchis.", "https://meronaddisababa.com/",
                        "+251 933 223344", "https://meronaddisababa.com/contact-us/",
                        List.of("Kazanchis", "store", "leather")),
                store("store_arada_mall_piassa", "Arada Mall", "Piassa",
                        "Piassa District, Addis Ababa, Ethiopia",
                        "Shopping, dining and lifestyle destination in Piassa.", "https://aradamall.com/",
                        "+251 911 466 466", "https://aradamall.com/", List.of("Piassa", "shopping", "store")),
                store("store_getu_bole", "Getu Commercial Center", "Bole",
                        "Near Bole, Addis Ababa, Ethiopia",
                        "Commercial destination with shops, offices, dining and services.",
                        "https://getucommercialcenter.lovable.app/", null,
                        "https://getucommercialcenter.lovable.app/", List.of("Bole", "shopping", "store")),

                event("event_great_ethiopian_run", "Great Ethiopian Run 2026", "Meskel Square",
                        "Meskel Square, Addis Ababa, Ethiopia", "10K road race and cultural carnival in Addis Ababa.",
                        "https://eventful.et/", "https://eventful.et/", "500", "ETB", "ticket",
                        List.of("Meskel Square", "sports", "event")),
                event("event_holiday_grand_bazaar", "Millennium Holiday Grand Bazaar & Expo", "Addis Ababa",
                        "Millennium Hall Complex, Addis Ababa, Ethiopia",
                        "Holiday shopping festival and family carnival.", "https://eventful.et/", "https://eventful.et/",
                        "150", "ETB", "ticket", List.of("Addis Ababa", "bazaar", "event")),
                event("event_auto_ev_expo", "Addis International Auto & EV Mobility Expo 2026", "Addis Ababa",
                        "Addis International Convention Center, Addis Ababa, Ethiopia",
                        "Automotive and electric mobility exhibition.", "https://eventful.et/", "https://eventful.et/",
                        null, "ETB", null, List.of("Addis Ababa", "exhibition", "event")),
                event("event_addis_music_festival", "Addis Music Festival 2026", "Addis Ababa",
                        "Ghion Hotel Open Lawns, Addis Ababa, Ethiopia",
                        "Music festival in Addis Ababa.", "https://eventful.et/", "https://eventful.et/",
                        null, "ETB", null, List.of("Addis Ababa", "music", "event")),
                event("event_techsummit_ethiopia", "TechSummit Ethiopia 2026", "Addis Ababa",
                        "Ethiopian Skylight Hotel Convention Center, Addis Ababa, Ethiopia",
                        "Technology and business summit in Addis Ababa.", "https://eventful.et/", "https://eventful.et/",
                        null, "ETB", null, List.of("Addis Ababa", "technology", "event"))
        );
    }

    private MarketplaceListing base(String id, String name, ListingCategory category, String area,
                                    String address, String description, String website, String phone,
                                    String price, String currency, String unit, String priceSource,
                                    String sourceUrl, List<String> tags) {
        return new MarketplaceListing(id, name, category, "Ethiopia", "Addis Ababa", area, address,
                description, website, phone, null, price, currency, unit, priceSource,
                SourceType.IMPORTED, name, sourceUrl, Instant.now(), tags);
    }

    private MarketplaceListing hotel(String id, String name, String area, String address, String description,
                                     String website, String phone, String sourceUrl, List<String> tags) {
        return base(id,name,ListingCategory.HOTEL,area,address,description,website,phone,null,"ETB",null,
                "NOT_PUBLISHED",sourceUrl,tags);
    }
    private MarketplaceListing car(String id, String name, String area, String address, String description,
                                   String website, String phone, String sourceUrl, String price, String currency,
                                   String unit, List<String> tags) {
        return base(id,name,ListingCategory.CAR,area,address,description,website,phone,price,currency,unit,
                price == null ? "NOT_PUBLISHED" : "PUBLISHED_SOURCE",sourceUrl,tags);
    }
    private MarketplaceListing store(String id, String name, String area, String address, String description,
                                     String website, String phone, String sourceUrl, List<String> tags) {
        return base(id,name,ListingCategory.STORE,area,address,description,website,phone,null,"ETB",null,
                "NOT_PUBLISHED",sourceUrl,tags);
    }
    private MarketplaceListing event(String id, String name, String area, String address, String description,
                                     String website, String sourceUrl, String price, String currency, String unit,
                                     List<String> tags) {
        return base(id,name,ListingCategory.EVENT,area,address,description,website,null,price,currency,unit,
                price == null ? "NOT_PUBLISHED" : "PUBLISHED_SOURCE",sourceUrl,tags);
    }
}
