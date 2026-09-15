package com.easyservice.discovery.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class LocationMatcher {

    public boolean matches(String city, String area, String text) {
        if (text == null) return false;
        String normalized = normalize(text);
        return normalized.contains(normalize(city))
                && normalized.contains(normalize(area));
    }

    private String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }
}
