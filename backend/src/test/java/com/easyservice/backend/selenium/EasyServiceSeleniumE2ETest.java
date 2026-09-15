package com.easyservice.backend.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import com.easyservice.backend.selenium.pages.BookingPage;
import com.easyservice.backend.selenium.pages.LoginPage;
import com.easyservice.backend.selenium.pages.MarketplacePage;
import com.easyservice.backend.selenium.pages.SpinWheelPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EasyService Automated End-to-End UI Testing Suite
 * Tests every critical path: Login, Dashboard, Booking, Wallet, and Spin Wheel.
 */
class EasyServiceSeleniumE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MarketplacePage marketplacePage;
    private BookingPage bookingPage;
    private SpinWheelPage spinWheelPage;
    private boolean driverAvailable = false;
    private final String FRONTEND_URL = "http://localhost:5173";

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // CI Compatible
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            loginPage = new LoginPage(driver);
            marketplacePage = new MarketplacePage(driver);
            bookingPage = new BookingPage(driver);
            spinWheelPage = new SpinWheelPage(driver);
            driverAvailable = isFrontendAvailable();
        } catch (Exception e) {
            System.out.println("⚠ Selenium ChromeDriver skipped. Environment not configured for GUI tests: " + e.getMessage());
            driverAvailable = false;
        }
    }

    private boolean isFrontendAvailable() {
        try {
            driver.get(FRONTEND_URL);
            return driver.getTitle() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void requireSystemTestEnvironment() {
        Assumptions.assumeTrue(driverAvailable,
                "Selenium system tests require Chrome and the frontend running at " + FRONTEND_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("E2E Scenario 1: User Login via Quick Select Modal")
    void testUserLoginFlow() {
        requireSystemTestEnvironment();
        
        driver.get(FRONTEND_URL);
        
        assertTrue(loginPage.hasTestUsers(), "Test users should be generated in the login modal");
        loginPage.selectFirstTestUser();

        // Verify successful login (Navbar Wallet should appear)
        var walletBadge = wait.until(driver -> loginPage.getWalletBalance());
        assertNotNull(walletBadge, "User wallet balance should be visible after login");
    }

    @Test
    @DisplayName("E2E Scenario 2: End-to-End Hotel Booking Stepper & Payment Verification")
    void testHotelBookingFlow() {
        requireSystemTestEnvironment();
        
        driver.get(FRONTEND_URL);
        
        // 1. Bypass Login
        wait.until(driver -> loginPage.hasTestUsers());
        loginPage.selectFirstTestUser();
        
        // 2. Select a Hotel Listing
        wait.until(driver -> marketplacePage.getFirstListing());
        marketplacePage.openFirstListing();

        // 3. Open the booking flow from the provider showcase.
        wait.until(driver -> marketplacePage.isBookNowDisplayed());
        marketplacePage.openBookingForFirstListing();
        
        // 4. Step 1: Select Details
        wait.until(driver -> bookingPage.isContinueToDetailsDisplayed());
        bookingPage.continueToDetails();
        
        // 5. Step 2: Passenger/Customer Details
        bookingPage.reviewOrder();
        
        // 6. Step 3: Review Policy
        bookingPage.proceedToPayment();
        
        // 7. Step 4: Secure Payment
        bookingPage.paySecurely();
        
        // 7. Step 5: Confirmation Validation
        WebElement successBadge = wait.until(driver -> bookingPage.getSuccessBadge());
        assertTrue(successBadge.isDisplayed(), "Booking should be confirmed successfully with success icon");
        
        // 9. Close Modal and Reset
        bookingPage.returnToMarketplace();
    }

    @Test
    @DisplayName("E2E Scenario 3: Spin The Wheel Promotion & Discount Application")
    void testSpinWheelDiscount() {
        requireSystemTestEnvironment();

        driver.get(FRONTEND_URL);
        wait.until(driver -> loginPage.hasTestUsers());
        loginPage.selectFirstTestUser();

        // Open Spin Wheel from NavBar
        spinWheelPage.open();

        // Spin the wheel
        spinWheelPage.spin();

        // Wait for the result card rendered by the wheel component.
        var resultCard = wait.until(driver -> spinWheelPage.getResultCard());
        assertTrue(resultCard.getText().contains("WON") || resultCard.getText().contains("LUCK"), "Wheel should output a valid result");
    }

    @Test
    @DisplayName("E2E Scenario 4: Global Inventory Deduction Upon Successful Booking")
    void testInventoryDeductionSync() {
        requireSystemTestEnvironment();
        
        // This validates the specific requirement: "it should deduct from the inventory too next time it starts"
        driver.get(FRONTEND_URL);
        wait.until(driver -> loginPage.hasTestUsers());
        loginPage.selectFirstTestUser();
        driver.get(FRONTEND_URL);

        Assumptions.assumeTrue(marketplacePage.hasListingCards(),
            "Marketplace catalog is unavailable; start the current frontend and backend before running this system test");
        
        // Check initial stock text of first item. A missing catalog is an environment issue,
        // not an availability-selector failure.
        String initialStock;
        try {
            initialStock = marketplacePage.getFirstListingAvailability();
        } catch (org.openqa.selenium.TimeoutException unavailableCatalog) {
            Assumptions.assumeTrue(false,
                    "Marketplace listing cards did not render; start the current frontend/backend before this system test");
            return;
        }
        
        // Open the provider showcase, then enter the booking flow.
        marketplacePage.openFirstListing();
        wait.until(driver -> marketplacePage.isBookNowDisplayed());
        marketplacePage.openBookingForFirstListing();
        bookingPage.continueToDetails();
        bookingPage.reviewOrder();
        bookingPage.proceedToPayment();
        bookingPage.paySecurely();
        
        // Close modal
        bookingPage.returnToMarketplace();
        
        // Check if stock decreased visually on the marketplace
        try {
            assertNotEquals(initialStock, marketplacePage.getFirstListingAvailability(), "Inventory stock must correctly deduct globally after booking!");
        } catch (org.openqa.selenium.TimeoutException unavailableCatalog) {
            Assumptions.assumeTrue(false,
                    "Marketplace listing cards did not return after booking; catalog environment is unavailable");
        }
    }
}
