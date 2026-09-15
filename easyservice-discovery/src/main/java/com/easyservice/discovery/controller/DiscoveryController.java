package com.easyservice.discovery.controller;

import com.easyservice.discovery.model.*;
import com.easyservice.discovery.service.DiscoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final DiscoveryService service;

    public DiscoveryController(DiscoveryService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("EasyService Discovery is running");
    }

    @PostMapping("/scan")
    public ResponseEntity<List<DiscoveredListing>> scan(
            @Valid @RequestBody DiscoveryRequest request) {
        return ResponseEntity.ok(service.discover(request));
    }
}
