package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BookingPage {
    private final WebDriver driver;

    private final By quantityField = By.id("quantityInput");
    private final By promoCodeField = By.id("promoCodeInput");
    private final By confirmBookingBtn = By.id("confirmBookingBtn");
    private final By bookingStatusText = By.id("bookingStatusLabel");
    private final By continueButton = By.xpath("//button[contains(., 'Continue to Details')]");
    private final By reviewButton = By.xpath("//button[contains(., 'Review Order')]");
    private final By paymentButton = By.xpath("//button[contains(., 'Proceed to Payment')]");
    private final By payButton = By.className("pay-btn");
    private final By successBadge = By.className("success-icon-badge");
    private final By doneButton = By.xpath("//button[contains(., 'Done & Back to Marketplace')]");

    public BookingPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterQuantity(int quantity) {
        driver.findElement(quantityField).clear();
        driver.findElement(quantityField).sendKeys(String.valueOf(quantity));
    }

    public void enterPromoCode(String promoCode) {
        driver.findElement(promoCodeField).sendKeys(promoCode);
    }

    public void clickConfirmBooking() {
        driver.findElement(confirmBookingBtn).click();
    }

    public String getBookingStatus() {
        return driver.findElement(bookingStatusText).getText();
    }

    public void continueToDetails() {
        driver.findElement(continueButton).click();
    }

    public boolean isContinueToDetailsDisplayed() {
        return driver.findElement(continueButton).isDisplayed();
    }

    public void reviewOrder() {
        driver.findElement(reviewButton).click();
    }

    public void proceedToPayment() {
        driver.findElement(paymentButton).click();
    }

    public void paySecurely() {
        driver.findElement(payButton).click();
    }

    public WebElement getSuccessBadge() {
        return driver.findElement(successBadge);
    }

    public void returnToMarketplace() {
        driver.findElement(doneButton).click();
    }
}
