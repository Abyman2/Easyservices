package com.easyservice.discovery.scraper;

import com.easyservice.discovery.model.*;
import com.easyservice.discovery.normalizer.ListingNormalizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

public abstract class BaseScraper {

    private final ListingNormalizer normalizer;
    private final boolean headless;
    private final long timeoutSeconds;
    private final String userAgent;

    protected BaseScraper(
            ListingNormalizer normalizer,
            @Value("${discovery.browser.headless:true}") boolean headless,
            @Value("${discovery.browser.timeout-seconds:20}") long timeoutSeconds,
            @Value("${discovery.user-agent:EasyServiceDiscovery/1.0}") String userAgent) {
        this.normalizer = normalizer;
        this.headless = headless;
        this.timeoutSeconds = timeoutSeconds;
        this.userAgent = userAgent;
    }

    protected abstract ListingCategory category();

    public List<DiscoveredListing> scrape(String url) {
        validateUrl(url);

        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--disable-gpu", "--no-sandbox",
                "--disable-dev-shm-usage", "--window-size=1440,1000");
        options.addArguments("--user-agent=" + userAgent);

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutSeconds));
            driver.get(url);

            Document doc = Jsoup.parse(driver.getPageSource(), url);
            String title = doc.title();
            String description = doc.select("meta[name=description]").attr("content");
            String image = doc.select("meta[property=og:image]").attr("content");
            String website = url;

            return List.of(normalizer.normalize(
                    category(), url, title, description, null, null,
                    null, website, image, null, null));
        } finally {
            driver.quit();
        }
    }

    private void validateUrl(String url) {
        if (url == null || !(url.startsWith("https://") || url.startsWith("http://"))) {
            throw new IllegalArgumentException("Only HTTP/HTTPS public URLs are supported");
        }
    }
}
