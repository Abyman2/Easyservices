package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CustomerDashboardPage {
    private final WebDriver driver;

    private final By userGreeting = By.id("userGreetingHeader");
    private final By identityBadge = By.id("identityStatusBadge");
    private final By balanceDisplay = By.id("userBalanceDisplay");

    public CustomerDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getUserGreeting() {
        return driver.findElement(userGreeting).getText();
    }

    public String getIdentityStatus() {
        return driver.findElement(identityBadge).getText();
    }

    public String getBalance() {
        return driver.findElement(balanceDisplay).getText();
    }
}
