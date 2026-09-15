package com.easyservice.discovery.model;

import java.util.List;

public record DiscoverySource(
        String name,
        ListingCategory category,
        List<String> urls,
        boolean enabled
) {}
