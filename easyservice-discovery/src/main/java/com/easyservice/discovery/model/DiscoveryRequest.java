package com.easyservice.discovery.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscoveryRequest(
        @NotBlank String url,
        @NotNull ListingCategory category
) {}
