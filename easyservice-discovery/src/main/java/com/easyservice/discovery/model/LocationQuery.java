package com.easyservice.discovery.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationQuery(
        @NotBlank String city,
        @NotBlank String area,
        @NotNull ListingCategory category
) {}
