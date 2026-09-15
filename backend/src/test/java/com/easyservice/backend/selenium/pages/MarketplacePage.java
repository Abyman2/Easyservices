package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MarketplacePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By listingCard = By.className("listing-card");
    private final By availabilityTag = By.className("availability-tag");
    private final By availabilityText = By.xpath(".//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'available') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'remaining')]");
    private final By bookNowButton = By.xpath("//button[contains(., 'Book Now')]");

    public MarketplacePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(listingCard));
        try {
            return card.findElement(availabilityTag).getText();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            try {
                return card.findElement(availabilityText).getText();
            } catch (org.openqa.selenium.NoSuchElementException stillMissing) {
                return card.getText();
            }
        }
    }

    public WebElement getFirstListing() {
        return driver.findElement(listingCard);
    }

    public boolean hasListingCards() {
        try {
            return wait.until(driver -> !driver.findElements(listingCard).isEmpty());
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return false;
        }
    }
}
