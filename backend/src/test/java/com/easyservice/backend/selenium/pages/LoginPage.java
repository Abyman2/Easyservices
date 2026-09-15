package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private final WebDriver driver;

    private final By emailField = By.id("emailInput");
    private final By passwordField = By.id("passwordInput");
    private final By loginButton = By.id("loginSubmitBtn");
    private final By errorMessage = By.id("loginErrorMessage");
    private final By testUserCard = By.className("test-user-card");
    private final By walletBalance = By.className("wallet-balance");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public void selectFirstTestUser() {
        driver.findElements(testUserCard).get(0).click();
    }

    public boolean hasTestUsers() {
        return !driver.findElements(testUserCard).isEmpty();
    }

    public WebElement getWalletBalance() {
        return driver.findElement(walletBalance);
    }
}
