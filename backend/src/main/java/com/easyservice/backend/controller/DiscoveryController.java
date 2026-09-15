package com.easyservice.backend.controller;

import com.easyservice.backend.service.DiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discovery")
@CrossOrigin(origins = "*")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @PostMapping("/search")
    public ResponseEntity<Object> search(
            @RequestBody DiscoveryRequest request) {

        Object result = discoveryService.search(
                request.city(),
                request.area(),
                request.category()
        );

        return ResponseEntity.ok(result);
    }

    public record DiscoveryRequest(
            String city,
            String area,
            String category
    ) {}
}