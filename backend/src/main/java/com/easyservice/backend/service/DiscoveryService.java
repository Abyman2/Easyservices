package com.easyservice.backend.service;

import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DiscoveryService {

    private final RestClient restClient;

    public DiscoveryService() {
        this(RestClient.builder().baseUrl("http://localhost:8090").build());
    }

    DiscoveryService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Object search(String city, String area, String category) {
        if ("ALL".equalsIgnoreCase(category)) {
            List<Object> results = new ArrayList<>();
            for (String supportedCategory : List.of("HOTEL", "CAR", "STORE", "EVENT")) {
                results.addAll(searchCategory(city, area, supportedCategory));
            }
            return results;
        }

        return searchCategory(city, area, category);
    }

    private List<Object> searchCategory(String city, String area, String category) {
        Map<String, String> request = Map.of(
                "city", city,
                "area", area,
                "category", category
        );

        return restClient.post()
                .uri("/api/discovery/addis/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Object>>() {});
    }
}