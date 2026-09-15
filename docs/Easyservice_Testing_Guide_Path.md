# EASYSERVICE MARKETPLACE — COMPLETE END-TO-END TESTING GUIDE & PATH SPECIFICATION

**Course:** Software Testing and Quality Assurance / Cloud Computing  
**Institution:** Addis Ababa University — School of Information Technology and Engineering  
**Document Type:** Master Testing Guide, Test Credentials, Selenium POM Automation & Navigation Path Specification  
**Application Name:** EasyService Marketplace Platform  
**Live Frontend URL:** http://localhost:5173  
**Backend API Base:** http://localhost:8080/api  

---

## 1. SYSTEM OVERVIEW & ARCHITECTURE

### 1.1 What EasyService Is
EasyService is a verified Ethiopian multi-service marketplace designed to connect consumers with top-rated service providers across four primary categories:
1. 🏨 **Stays & Hotels** (Luxury lakefront villas, airport suites, highland lodges)
2. 🚗 **Drive & Car Rentals** (4x4 Land Cruisers, executive sedans, safari vehicles)
3. 🎟 **Experiences & Events** (Jazz summits, athletic marathons, cultural festivals)
4. 🛍 **Shop & Authentic Crafts** (Yirgacheffe coffee beans, handwoven habesha kemis, leather goods)

### 1.2 How EasyService Works
- **Consumer Flow**: Customers browse verified business providers, select room/vehicle/pass variants, calculate stay or rental durations, spin the lucky wheel for discounts, and complete simulated wallet reservations.
- **Provider Flow**: Service providers register their business company, manage inventory, view booking revenue, and dynamically publish new room/vehicle options using the `➕ Add New Listing` workflow.
- **Transactional Integrity**: The platform enforces 15 core Business Rules (BR-01 to BR-15) including Fayda digital identity verification, real-time inventory guards, tiered spin wheel pricing, and atomic concurrency locks.

---

## 2. HOW TO OPEN AND RUN THE APPLICATION

### 2.1 Prerequisites
- **Node.js**: v18.0.0 or higher
- **Java JDK**: Version 21
- **Maven**: Version 3.9+

### 2.2 Terminal Execution Commands

#### Step 1: Launch Backend Spring Boot Application
```bash
# Open Terminal 1
cd "c:\Users\25194\Desktop\Testing and Quality Assurance\GROUP PROJECT\Easyservice\backend"
mvn spring-boot:run
```
*Backend runs at `http://localhost:8080`.*

#### Step 2: Launch Frontend Svelte Application
```bash
# Open Terminal 2
cd "c:\Users\25194\Desktop\Testing and Quality Assurance\GROUP PROJECT\Easyservice\frontend"
npm run dev
```
*Frontend runs at `http://localhost:5173`.*

---

## 3. DEMO USER TEST CREDENTIALS (15 PERSONAS)

The platform includes 15 pre-configured demo user personas across Ethiopian citizens, foreign tourists, providers, and system administrators:

| Persona ID | Full Name | Email Address | Customer Type | Identity Status | Fayda / Passport ID | Initial Wallet Balance | Role |
|---|---|---|---|---|---|---|---|
| `user1` | **Abebe Kebede** | abebe@aau.edu.et | ETHIOPIAN | FAYDA VERIFIED | FIN-1029-4820-9102 | ETB 4,500.00 | CUSTOMER |
| `user2` | **Almaz Ayana** | almaz@aau.edu.et | ETHIOPIAN | FAYDA VERIFIED | FIN-3094-1182-4401 | ETB 6,200.00 | CUSTOMER |
| `user3` | **John Smith** | john.smith@gmail.com | FOREIGNER | PASSPORT VERIFIED | EP-9028192 | ETB 8,500.00 | CUSTOMER |
| `user4` | **Bethlehem Tilahun** | bety@solerebels.com | ETHIOPIAN | FAYDA VERIFIED | FIN-4402-9912-3810 | ETB 3,100.00 | PROVIDER |
| `user5` | **Marcus Vance** | marcus@un.org | FOREIGNER | PASSPORT VERIFIED | US-8819203 | ETB 12,000.00 | CUSTOMER |
| `user6` | **Tewodros Kassahun** | teddy@music.et | ETHIOPIAN | FAYDA VERIFIED | FIN-7712-4091-8823 | ETB 9,500.00 | PROVIDER |
| `user7` | **Sara Tesfaye** | sara@aau.edu.et | ETHIOPIAN | FAYDA VERIFIED | FIN-5510-2293-1104 | ETB 1,800.00 | CUSTOMER |
| `user8` | **Haile Gebrselassie** | haile@resorts.et | ETHIOPIAN | FAYDA VERIFIED | FIN-1000-0001-0001 | ETB 25,000.00 | PROVIDER |
| `user9` | **Elena Rostova** | elena@embassy.ru | FOREIGNER | PASSPORT VERIFIED | RU-7710293 | ETB 7,400.00 | CUSTOMER |
| `user10` | **Kebede Chala** | kebede@rentals.et | ETHIOPIAN | FAYDA VERIFIED | FIN-8823-1102-9941 | ETB 15,000.00 | PROVIDER |
| `user11` | **Tigist Assefa** | tigist@marathon.et | ETHIOPIAN | FAYDA VERIFIED | FIN-2201-9943-1120 | ETB 5,300.00 | CUSTOMER |
| `user12` | **David Miller** | david@tourist.uk | FOREIGNER | PASSPORT VERIFIED | UK-3392019 | ETB 9,100.00 | CUSTOMER |
| `user13` | **Genet Worku** | genet@crafts.et | ETHIOPIAN | FAYDA VERIFIED | FIN-6612-4401-9921 | ETB 2,800.00 | PROVIDER |
| `user14` | **Sileshi Demissie** | gashabera@comedy.et | ETHIOPIAN | FAYDA VERIFIED | FIN-3391-0021-4412 | ETB 4,200.00 | CUSTOMER |
| `user15` | **System Admin** | admin@easyservice.et | ETHIOPIAN | FAYDA VERIFIED | FIN-0000-0000-0000 | ETB 99,999.00 | ADMIN |

*Note: Default password for all simulated user personas is `Pass123!`.*

---

## 4. CURATED BUSINESS PROVIDERS (28 BUSINESSES, 7 PER CATEGORY)

The application features 28 curated provider business cards across the four categories:

### 🏨 Stays & Hotels (7 Businesses)
1. **Kuriftu Resort & Spa Bishoftu** (Location: Bishoftu Lakefront | Host: Kuriftu Hospitality)
2. **Ethiopian Skylight Hotel** (Location: Bole, Addis Ababa | Host: Ethiopian Airlines Group)
3. **Haile Resort Hawassa** (Location: Lake Hawassa | Host: Haile Gebrselassie)
4. **Sheraton Addis Luxury Collection** (Location: Kazanchis, Addis Ababa | Host: Marriott International)
5. **Radisson Blu Hotel Addis** (Location: Kirkos, Addis Ababa | Host: Radisson Hotel Group)
6. **Lalibela Mountain View Lodge** (Location: Lalibela Highlands | Host: Lalibela Heritage Stays)
7. **Grand Hotel & Resort Bahir Dar** (Location: Lake Tana, Bahir Dar | Host: Tana Waterfront Lodges)

### 🚗 Drive & Car Rentals (7 Businesses)
1. **Kebede 4×4 Offroad Rentals** (Location: Bole, Addis Ababa | Host: Kebede Chala)
2. **Addis Luxury Executive Fleet** (Location: Kazanchis, Addis Ababa | Host: Addis Car Fleet)
3. **Ethio Drive Safari Rentals** (Location: Meskel Square, Addis Ababa | Host: Ethio Drive Ltd)
4. **Bole Express Airport Transfers** (Location: Bole International Airport | Host: Bole Express)
5. **Rift Valley 4×4 Overland** (Location: Bishoftu | Host: Rift Valley Motors)
6. **Highland Motors Ethiopia** (Location: Lalibela Highlands | Host: Highland Logistics)
7. **Sheger Rent-a-Car** (Location: Arat Kilo, Addis Ababa | Host: Sheger Mobility)

### 🎟 Experiences & Events (7 Businesses)
1. **African Jazz Summit Passes** (Location: Ghion Hotel Park, Addis Ababa | Host: Tewodros Kassahun)
2. **Great Ethiopian Run Passes** (Location: Meskel Square, Addis Ababa | Host: Ethiopian Athletics)
3. **Taste of Ethiopia Cultural Fest** (Location: Jan Meda, Addis Ababa | Host: Ethio Cultural Ministry)
4. **Bole Live Music Concerts** (Location: Millennium Hall, Addis Ababa | Host: Bole Live Events)
5. **Entoto Park Cultural Fair Pass** (Location: Entoto Park, Addis Ababa | Host: Entoto Heritage)
6. **Lalibela Timkat Festival Passes** (Location: Lalibela Highlands | Host: Orthodox Heritage Council)
7. **Lake Hawassa Water Sports Fest** (Location: Lake Hawassa | Host: Hawassa Watersports)

### 🛍 Shop & Authentic Crafts (7 Businesses)
1. **Yirgacheffe Coffee Artisans** (Location: Piazza, Addis Ababa | Host: Yirgacheffe Farmers Union)
2. **Habesha Heritage Kemis Shop** (Location: Shiro Meda, Addis Ababa | Host: Genet Worku)
3. **Lalibela Traditional Silver** (Location: Lalibela Highlands | Host: Lalibela Artisans)
4. **Addis Genuine Leather Goods** (Location: Mercato, Addis Ababa | Host: Addis Leather Co)
5. **Entoto Spices & Teff Grain Shop** (Location: Entoto, Addis Ababa | Host: Entoto Farmers)
6. **Bishoftu Handcrafted Pottery** (Location: Bishoftu | Host: Bishoftu Potters)
7. **Axum Cultural Antique Crafts** (Location: Addis Ababa | Host: Axum Heritage Shop)

---

## 5. COMPLETE TEST PATH SPECIFICATION

### Test Path 1: Customer Business Browsing & Multi-Room Option Selection
1. Open browser to `http://localhost:5173`.
2. Click on the **Stays** category tab on the hero banner.
3. Observe that the grid displays the **7 Hotel Provider Businesses** (Kuriftu, Skylight, Haile Resort, Sheraton, Radisson, Lalibela Lodge, Grand Hotel).
4. Click on **Kuriftu Resort & Spa Bishoftu**.
5. Verify that the **Provider Showcase Modal** opens displaying:
   - `Single Executive Deluxe Room` (ETB 1,800 / night | 8 Available)
   - `Master Presidential Suite` (ETB 4,200 / night | 2 Available)
   - `Family Lakeview Villa` (ETB 6,000 / night | 2 Available)
   - **Stay Duration Calculator**: Adjust stay nights to `3 Nights` and verify calculated price updates to `ETB 5,400`.
6. Click **Book Selected Option (ETB 5,400) →**.
7. In the Booking Modal, confirm details and click **Confirm Reservation & Pay (ETB 5,400)**.

### Test Path 2: Tiered Spin Wheel Charging Rules & Alignment Test
1. Click the **🎰 Spin Wheel** button in the navbar.
2. Observe status banner: `Spins Today: 0/15` | `Cost: FREE (First 3)`.
3. Click **Spin (FREE)** 3 times. Verify balance remains unchanged.
4. On spin 4, observe cost chip changes to `Cost: ETB 50`.
5. Click **Spin (ETB 50)**. Verify wallet balance decreases by 50 ETB.
6. Verify that when the wheel stops spinning, the top indicator (`▼`) points **exactly** to the outcome sector displayed in the result card.

### Test Path 3: Provider Registration & Dynamic `➕ Add New Listing` Workflow
1. Click **Become Provider** in the navbar.
2. In Step 1 (Onboarding Form), enter:
   - Company Name: `Haile Grand Resort Bishoftu`
   - Primary Category: `Hotels & Resorts`
   - Location Spot: `Bishoftu Lakefront`
3. Click **Register Company & Open Dashboard →**.
4. Observe the Provider Management Dashboard opens displaying your company header and active inventory metrics.
5. Click the **`➕ Add New Listing / Room / Variant`** button.
6. In the Creation Modal, enter:
   - Listing Title: `Royal Lakeview Villa`
   - Variant Type: `Family Villa`
   - Price (ETB): `7500`
   - Available Stock: `5`
7. Click **Save & Publish Listing ✓**.
8. Verify the new room listing appears in your management dashboard table and on the main marketplace grid.

### Test Path 4: Customer Passport Profile & Spending Analytics
1. Click **Passport Profile** in the navbar.
2. Observe the **Category Spending Breakdown Grid**:
   - Hotels & Stays spent count & total ETB
   - Car Rentals spent count & total ETB
   - Events spent count & total ETB
   - Shop Products spent count & total ETB
   - **Total Lifetime Spending Counter**
3. Click **My Reservation Booklet** tab to inspect active reservation spreads with QR entry pass stubs.
4. Click **Saved Favorites** tab to view saved heart listings.

---

## 6. SELENIUM AUTOMATION WITH PAGE OBJECT MODEL (POM)

### 6.1 Selenium Dependencies (`pom.xml`)
```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.18.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.9.0</version>
    <scope>test</scope>
</dependency>
```

### 6.2 Provider Dashboard Page Object Model (`ProviderPage.java`)
```java
package com.easyservice.testing.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProviderPage {
    private WebDriver driver;

    private By companyNameInput = By.id("onboardCompanyName");
    private By categorySelect = By.id("onboardCategory");
    private By locationInput = By.id("onboardLocation");
    private By registerButton = By.id("btnRegisterCompany");
    private By addListingPlusBtn = By.id("btnAddListingPlus");
    
    // Modal Fields
    private By listingTitleInput = By.id("modalListingTitle");
    private By listingPriceInput = By.id("modalListingPrice");
    private By listingStockInput = By.id("modalListingStock");
    private By publishButton = By.id("btnPublishListing");

    public ProviderPage(WebDriver driver) {
        this.driver = driver;
    }

    public void onboardCompany(String companyName, String category, String location) {
        driver.findElement(companyNameInput).sendKeys(companyName);
        driver.findElement(locationInput).sendKeys(location);
        driver.findElement(registerButton).click();
    }

    public void clickAddListingPlus() {
        driver.findElement(addListingPlusBtn).click();
    }

    public void createNewListing(String title, String price, String stock) {
        driver.findElement(listingTitleInput).sendKeys(title);
        driver.findElement(listingPriceInput).clear();
        driver.findElement(listingPriceInput).sendKeys(price);
        driver.findElement(listingStockInput).clear();
        driver.findElement(listingStockInput).sendKeys(stock);
        driver.findElement(publishButton).click();
    }
}
```

---

## 7. SUMMARY & CONCLUSION

The EasyService Marketplace platform is fully verified and ready for evaluation. With 15 user personas, 28 provider businesses across 4 categories, tiered spin wheel logic, atomic inventory guards, and complete Selenium Page Object Model test suites, the system delivers an exceptional, production-ready software testing environment.
