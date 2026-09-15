package com.easyservice.discovery.service;

import com.easyservice.discovery.model.*;
import com.easyservice.discovery.scraper.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryService {

    private final HotelScraper hotels;
    private final CarScraper cars;
    private final StoreScraper stores;
    private final EventScraper events;

    public DiscoveryService(HotelScraper hotels, CarScraper cars,
                            StoreScraper stores, EventScraper events) {
        this.hotels = hotels;
        this.cars = cars;
        this.stores = stores;
        this.events = events;
    }

    public List<DiscoveredListing> discover(DiscoveryRequest request) {
        return switch (request.category()) {
            case HOTEL -> hotels.scrape(request.url());
            case CAR -> cars.scrape(request.url());
            case STORE -> stores.scrape(request.url());
            case EVENT -> events.scrape(request.url());
        };
    }
}
