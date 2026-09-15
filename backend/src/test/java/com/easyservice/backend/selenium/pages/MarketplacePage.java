package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MarketplacePage {
    private final WebDriver driver;

    private final By listingCard = By.className("listing-card");
    private final By availabilityTag = By.className("availability-tag");
    private final By bookNowButton = By.xpath("//button[contains(., 'Book Now')]");

    public MarketplacePage(WebDriver driver) {
        this.driver = driver;
    }

    public void openFirstListing() {
        driver.findElement(listingCard).click();
    }

    public void openBookingForFirstListing() {
        driver.findElement(bookNowButton).click();
    }

    public boolean isBookNowDisplayed() {
        return driver.findElement(bookNowButton).isDisplayed();
    }

    public String getFirstListingAvailability() {
        return driver.findElement(availabilityTag).getText();
    }

    public WebElement getFirstListing() {
        return driver.findElement(listingCard);
    }
}
