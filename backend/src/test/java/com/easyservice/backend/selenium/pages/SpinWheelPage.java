package com.easyservice.backend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SpinWheelPage {
    private final WebDriver driver;

    private final By openButton = By.className("promo-wheel-btn");
    private final By spinButton = By.className("spin-btn");
    private final By resultCard = By.className("result-card");

    public SpinWheelPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.findElement(openButton).click();
    }

    public void spin() {
        driver.findElement(spinButton).click();
    }

    public WebElement getResultCard() {
        return driver.findElement(resultCard);
    }
}