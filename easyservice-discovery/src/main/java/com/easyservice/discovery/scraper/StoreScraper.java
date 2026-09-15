package com.easyservice.discovery.scraper;

import com.easyservice.discovery.model.ListingCategory;
import com.easyservice.discovery.normalizer.ListingNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StoreScraper extends BaseScraper {
    public StoreScraper(ListingNormalizer n, @Value("${discovery.browser.headless:true}") boolean h,
                        @Value("${discovery.browser.timeout-seconds:20}") long t,
                        @Value("${discovery.user-agent:EasyServiceDiscovery/1.0}") String u) {
        super(n, h, t, u);
    }
    protected ListingCategory category() { return ListingCategory.STORE; }
}
