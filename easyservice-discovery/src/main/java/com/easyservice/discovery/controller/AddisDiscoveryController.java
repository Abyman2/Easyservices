package com.easyservice.discovery.controller;

import com.easyservice.discovery.model.LocationQuery;
import com.easyservice.discovery.model.MarketplaceListing;
import com.easyservice.discovery.service.AddisDiscoveryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery/addis")
@CrossOrigin(origins = "*")
public class AddisDiscoveryController {

    private final AddisDiscoveryService discoveryService;

    public AddisDiscoveryController(AddisDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping("/sources")
    public List<com.easyservice.discovery.model.DiscoverySource> sources() {
        return discoveryService.getSources();
    }

    @PostMapping("/search")
    public List<MarketplaceListing> search(@Valid @RequestBody LocationQuery query) {
        return discoveryService.discover(query);
    }
}
