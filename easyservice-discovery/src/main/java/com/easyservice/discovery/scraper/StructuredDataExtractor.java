package com.easyservice.discovery.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Optional;

/**
 * Small reusable extractor for public structured data.
 * It prefers Schema.org JSON-LD and OpenGraph/meta data.
 *
 * It intentionally returns Optional values: the marketplace must never
 * fabricate a phone, image, website or price when the source does not publish it.
 */
public final class StructuredDataExtractor {

    private StructuredDataExtractor() {}

    public static Document parse(String html) {
        return Jsoup.parse(html);
    }

    public static Optional<String> meta(Document doc, String property) {
        Element element = doc.selectFirst("meta[property='" + property + "']");
        if (element == null) {
            element = doc.selectFirst("meta[name='" + property + "']");
        }
        if (element == null) return Optional.empty();

        String value = element.attr("content");
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    public static Optional<String> canonicalUrl(Document doc) {
        Element link = doc.selectFirst("link[rel=canonical]");
        if (link == null) return Optional.empty();

        String value = link.attr("href");
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }
}
