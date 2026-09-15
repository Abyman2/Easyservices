package com.easyservice.discovery.source;

import com.easyservice.discovery.model.DiscoverySource;
import com.easyservice.discovery.model.ListingCategory;
import com.easyservice.discovery.model.MarketplaceListing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class PublicSourceCrawler {
    private final String userAgent;
    private final int timeoutMs;

    public PublicSourceCrawler(
            @Value("${discovery.user-agent:EasyServiceDiscovery/1.1}") String userAgent,
            @Value("${easyservice.discovery.http-timeout-ms:12000}") int timeoutMs) {
        this.userAgent = userAgent;
        this.timeoutMs = Math.max(3000, timeoutMs);
    }

    public List<MarketplaceListing> crawl(DiscoverySource source, String city, String area) {
        List<MarketplaceListing> out = new ArrayList<>();
        for (String url : source.urls()) {
            try {
                if (!allowedByRobots(url)) continue;
                Document doc = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .timeout(timeoutMs)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .get();
                int beforeCount = out.size();

                extractJsonLd(doc, source, city, area, out);

// Some public sites, especially Squarespace sites, may not expose
// a usable Hotel/LodgingBusiness JSON-LD object.
// Fall back to visible public HTML instead of losing the business.
                if (out.size() == beforeCount) {
                    MarketplaceListing fallback = extractHtmlFallback(
                            doc,
                            source,
                            city,
                            area,
                            url
                    );

                    if (fallback != null) {
                        out.add(fallback);
                    }
                }
            } catch (Exception ignored) {
                // One blocked/down source must never break the entire discovery request.
            }
        }
        return dedupe(out);
    }

    private void extractJsonLd(Document doc, DiscoverySource source, String city, String area,
                                List<MarketplaceListing> out) {
        for (Element script : doc.select("script[type=application/ld+json]")) {
            String json = script.data();
            if (json == null || json.isBlank()) continue;
            try {
                Object root = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Object.class);
                walk(root, source, city, area, out, doc.location());
            } catch (Exception ignored) {}
        }
    }

    private MarketplaceListing extractHtmlFallback(
            Document doc,
            DiscoverySource source,
            String city,
            String area,
            String pageUrl) {

        String pageText = doc.text();

        // The public page still has to match the requested location.
        if (!matchesLocation(city, area, pageText)) {
            return null;
        }

        String name = source.name();

        if (name == null || name.isBlank()) {
            return null;
        }

        Element descriptionMeta =
                doc.selectFirst("meta[name=description]");

        String description = descriptionMeta == null
                ? null
                : clean(descriptionMeta.attr("content"));

        Element canonical =
                doc.selectFirst("link[rel=canonical]");

        String website = canonical == null
                ? pageUrl
                : clean(canonical.attr("href"));

        if (website == null) {
            website = pageUrl;
        }

        Element phoneLink =
                doc.selectFirst("a[href^=tel:]");

        String phone = null;

        if (phoneLink != null) {
            phone = phoneLink.attr("href")
                    .replaceFirst("(?i)^tel:", "")
                    .trim();

            if (phone.isBlank()) {
                phone = null;
            }
        }

        // Use the real public HTML images.
        // pageImages() already understands Squarespace data-image/data-src.
        List<String> images = pageImages(pageUrl);

        images = new ArrayList<>(
                new LinkedHashSet<>(images)
        );

        if (images.size() > 12) {
            images = new ArrayList<>(
                    images.subList(0, 12)
            );
        }

        String image = images.isEmpty()
                ? null
                : images.get(0);

        String discoveredArea =
                areaOrExtract(area, pageText);

        String id = stableId(
                source.category(),
                name,
                website
        );

        return new MarketplaceListing(
                id,
                clean(name),
                source.category(),
                "Ethiopia",
                city,
                discoveredArea,
                null,
                description,
                clean(website),
                clean(phone),
                clean(image),
                null,
                "ETB",
                null,
                "NOT_PUBLISHED",
                com.easyservice.discovery.model.SourceType.IMPORTED,
                source.name(),
                pageUrl,
                Instant.now(),
                List.of(
                        "live-source",
                        source.name()
                ),
                images
        );
    }

    @SuppressWarnings("unchecked")
    private void walk(Object node, DiscoverySource source, String city, String area,
                      List<MarketplaceListing> out, String pageUrl) {
        if (node instanceof List<?> list) {
            for (Object item : list) walk(item, source, city, area, out, pageUrl);
            return;
        }
        if (!(node instanceof Map<?, ?> raw)) return;
        Map<String,Object> map = (Map<String,Object>) raw;

        Object graph = map.get("@graph");
        if (graph != null) walk(graph, source, city, area, out, pageUrl);

        String type = text(map.get("@type"));
        if (isCandidateType(type, source.category())) {
            MarketplaceListing listing = toListing(map, source, city, area, pageUrl);
            if (listing != null) out.add(listing);
        }
        for (Object value : map.values()) {
            if (value instanceof Map<?,?> || value instanceof List<?>) {
                walk(value, source, city, area, out, pageUrl);
            }
        }
    }

    private boolean isCandidateType(String type, ListingCategory category) {
        String t = type == null ? "" : type.toLowerCase(Locale.ROOT);
        return switch (category) {
            case HOTEL -> t.contains("hotel") || t.contains("lodgingbusiness");
            case CAR -> t.contains("carrental") || t.contains("automotivebusiness");
            case STORE -> t.contains("store") || t.contains("shoppingcenter") || t.contains("localbusiness");
            case EVENT -> t.contains("event");
        };
    }

    private MarketplaceListing toListing(Map<String,Object> m, DiscoverySource source,
                                          String city, String area, String pageUrl) {
        String name = first(m, "name", "headline");
        if (name == null) return null;

        String address = addressText(m.get("address"));
        String textForLocation = String.join(" ", name, address == null ? "" : address,
                text(m.get("description")));
        if (!matchesLocation(city, area, textForLocation)) return null;

        String website = first(m, "url", "sameAs");
        if (website == null) website = pageUrl;
        String phone = first(m, "telephone", "phone");
        String description = first(m, "description");

        List<String> images = imageTexts(m.get("image"), pageUrl);

        if (m.get("photo") != null) {
            images.addAll(imageTexts(m.get("photo"), pageUrl));
        }

// Also look for the website's Open Graph preview image.
        String ogImage = openGraphImage(pageUrl);

        if (ogImage != null) {
            images.add(ogImage);
        }

// Finally inspect normal HTML images on the public page.
        images.addAll(pageImages(pageUrl));

        images = new ArrayList<>(new LinkedHashSet<>(images));

        if (images.size() > 12) {
            images = new ArrayList<>(images.subList(0, 12));
        }

        String image = images.isEmpty() ? null : images.get(0);

        Map<String,Object> offers = map(m.get("offers"));
        String price = first(offers, "price", "lowPrice");
        String currency = first(offers, "priceCurrency");
        if (price == null) {
            price = first(m, "price");
        }

        String sourceUrl = website == null ? pageUrl : website;
        String id = stableId(source.category(), name, sourceUrl);
        String priceSource = price == null ? "NOT_PUBLISHED" : "PUBLISHED_SOURCE";
        Map<String, Object> geo = map(m.get("geo"));
        Double latitude = number(geo.get("latitude"));
        Double longitude = number(geo.get("longitude"));

        return new MarketplaceListing(
                id,
                clean(name),
                source.category(),
                "Ethiopia",
                city,
                areaOrExtract(area, address),
                clean(address),
                clean(description),
                clean(website),
                clean(phone),
                clean(image),
                clean(price),
                currency == null ? "ETB" : clean(currency),
                price == null ? null : "published",
                priceSource,
                com.easyservice.discovery.model.SourceType.IMPORTED,
                source.name(),
                pageUrl,
                Instant.now(),
                List.of("live-source", source.name()),
                images,
                latitude,
                longitude
        );
    }

    private Double number(Object value) {
        if (value instanceof Number number) return number.doubleValue();
        if (value == null) return null;
        try { return Double.parseDouble(String.valueOf(value)); } catch (NumberFormatException ignored) { return null; }
    }

    private boolean allowedByRobots(String targetUrl) {
        try {
            java.net.URI target = java.net.URI.create(targetUrl);
            String origin = target.getScheme() + "://" + target.getHost();
            Document robots = Jsoup.connect(origin + "/robots.txt").userAgent(userAgent)
                    .timeout(timeoutMs).ignoreHttpErrors(true).get();
            String path = target.getRawPath();
            if (path == null || path.isBlank()) path = "/";
            boolean matchedAgent = false;
            for (String line : robots.text().split("\\R")) {
                String cleaned = line.trim();
                if (cleaned.isEmpty() || cleaned.startsWith("#")) continue;
                String lower = cleaned.toLowerCase(Locale.ROOT);
                if (lower.startsWith("user-agent:")) {
                    matchedAgent = lower.substring(11).trim().equals("*");
                } else if (matchedAgent && lower.startsWith("disallow:")) {
                    String rule = cleaned.substring(cleaned.indexOf(':') + 1).trim();
                    if (!rule.isEmpty() && path.startsWith(rule)) return false;
                }
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    private boolean matchesLocation(String city, String area, String text) {
        String n = normalize(text);
        if (!n.contains(normalize(city))) return false;
        if ("ALL".equalsIgnoreCase(area) || "*".equals(area)) return true;
        return n.contains(normalize(area));
    }

    private String areaOrExtract(String requestedArea, String address) {
        if (!"ALL".equalsIgnoreCase(requestedArea) && !"*".equals(requestedArea)) return requestedArea;
        String n = address == null ? "" : address.toLowerCase(Locale.ROOT);
        for (String candidate : List.of("bole", "kazanchis", "22 mazoria", "piassa", "meskel square",
                "megenagna", "cmc", "saris", "arada", "kirkos", "yeka", "lideta", "mexico")) {
            if (n.contains(candidate)) return candidate;
        }
        return "Addis Ababa";
    }

    private List<MarketplaceListing> dedupe(List<MarketplaceListing> input) {
        Map<String,MarketplaceListing> map = new LinkedHashMap<>();
        for (MarketplaceListing l : input) {
            String key = normalize(l.name()) + "|" + normalize(l.website());
            map.putIfAbsent(key, l);
        }
        return new ArrayList<>(map.values());
    }

    private String stableId(ListingCategory c, String name, String url) {
        return "live_" + c.name().toLowerCase(Locale.ROOT) + "_" +
                Integer.toUnsignedString((normalize(name) + "|" + normalize(url)).hashCode());
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> map(Object value) {
        return value instanceof Map<?,?> m ? (Map<String,Object>) m : Collections.emptyMap();
    }

    private String addressText(Object value) {
        if (value instanceof String s) return s;
        if (!(value instanceof Map<?,?> m)) return null;
        List<String> parts = new ArrayList<>();
        for (String k : List.of("streetAddress","addressLocality","addressRegion","postalCode","addressCountry")) {
            Object v = m.get(k);
            if (v != null && !v.toString().isBlank()) parts.add(v.toString());
        }
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private String openGraphImage(String pageUrl) {
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .get();

            Element image = doc.selectFirst("meta[property=og:image]");

            if (image == null) {
                image = doc.selectFirst("meta[name=twitter:image]");
            }

            if (image == null) {
                return null;
            }

            String content = image.attr("content");

            return resolveUrl(content, pageUrl);

        } catch (Exception ignored) {
            return null;
        }
    }

    private List<String> pageImages(String pageUrl) {
        List<String> images = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .get();

            for (Element img : doc.select("img")) {

                String url = firstNonBlank(
                        img.attr("data-image"),
                        img.attr("data-src"),
                        img.attr("data-lazy-src"),
                        img.attr("src"),
                        firstSrcSetUrl(img.attr("srcset"))
                );

                if (url == null || url.isBlank()) {
                    continue;
                }

                String resolved = resolveUrl(url, pageUrl);

                if (resolved == null) {
                    continue;
                }

                String searchable = (
                        img.attr("alt") + " " +
                                img.attr("title") + " " +
                                resolved
                ).toLowerCase(Locale.ROOT);

                // Skip obvious non-photo assets.
                if (searchable.contains("logo")
                        || searchable.contains("icon")
                        || searchable.contains("favicon")
                        || searchable.contains("sprite")
                        || searchable.endsWith(".svg")
                        || searchable.contains("arrow-down")) {
                    continue;
                }

                // Avoid duplicate images.
                if (!images.contains(resolved)) {
                    images.add(resolved);
                }

                if (images.size() >= 12) {
                    break;
                }
            }

        } catch (Exception ignored) {
            // HTML image extraction is optional.
        }

        return images;
    }

    private String firstSrcSetUrl(String srcSet) {
        if (srcSet == null || srcSet.isBlank()) {
            return null;
        }

        String first = srcSet.split(",")[0].trim();

        if (first.isBlank()) {
            return null;
        }

        String[] parts = first.split("\\s+");

        return parts.length > 0 ? parts[0] : null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> imageTexts(Object value, String pageUrl) {
        List<String> images = new ArrayList<>();

        if (value instanceof String s) {
            String url = resolveUrl(s, pageUrl);

            if (url != null) {
                images.add(url);
            }

            return images;
        }

        if (value instanceof List<?> list) {
            for (Object item : list) {
                images.addAll(imageTexts(item, pageUrl));
            }

            return images;
        }

        if (value instanceof Map<?, ?> raw) {
            Map<String, Object> map = (Map<String, Object>) raw;

            String url = first(map, "url", "contentUrl");

            if (url != null) {
                String resolved = resolveUrl(url, pageUrl);

                if (resolved != null) {
                    images.add(resolved);
                }
            }
        }

        return images;
    }

    private String resolveUrl(String value, String pageUrl) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            java.net.URI imageUri = java.net.URI.create(value.trim());

            if (imageUri.isAbsolute()) {
                return imageUri.toString();
            }

            return java.net.URI.create(pageUrl).resolve(imageUri).toString();

        } catch (Exception ignored) {
            return null;
        }
    }

    private String first(Map<String,Object> m, String... keys) {
        for (String key : keys) {
            Object v = m.get(key);
            if (v instanceof List<?> l && !l.isEmpty()) v = l.get(0);
            if (v instanceof Map<?,?> mm) {
                Object u = mm.get("url");
                if (u != null) v = u;
            }
            if (v != null && !v.toString().isBlank()) return v.toString();
        }
        return null;
    }

    private String text(Object v) { return v == null ? "" : v.toString(); }
    private String clean(String v) { return v == null || v.isBlank() ? null : v.trim(); }

    private String normalize(String v) {
        if (v == null) return "";
        return java.text.Normalizer.normalize(v, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ").trim();
    }
}
