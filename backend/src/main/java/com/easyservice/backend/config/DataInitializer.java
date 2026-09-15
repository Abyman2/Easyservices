package com.easyservice.backend.config;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.Promotion;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.*;
import com.easyservice.backend.repository.ListingRepository;
import com.easyservice.backend.repository.PromotionRepository;
import com.easyservice.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final PromotionRepository promotionRepository;

    public DataInitializer(UserRepository userRepository,
                           ListingRepository listingRepository,
                           PromotionRepository promotionRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public void run(String... args) {
        // Seed 10 Users (8 Ethiopian Verified, 2 Foreigner Verified)
        for (int i = 1; i <= 8; i++) {
            User user = new User(
                    "cust_" + i,
                    "Customer " + i + " (Ethiopian)",
                    "user" + i + "@aau.edu.et",
                    "+25191100000" + i,
                    "Password123!",
                    "Ethiopia",
                    CustomerType.ETHIOPIAN,
                    IdentityType.FAYDA,
                    "FY1000" + i,
                    IdentityStatus.VERIFIED,
                    BigDecimal.valueOf(5000.00 + (i * 500))
            );
            userRepository.save(user);
        }

        // 2 Foreigner Customers
        userRepository.save(new User("cust_9", "John Foreigner", "john@global.com", "+12025550199",
                "Password123!", "USA", CustomerType.FOREIGNER, IdentityType.PASSPORT,
                "P90001", IdentityStatus.VERIFIED, BigDecimal.valueOf(8000.00)));

        userRepository.save(new User("cust_10", "Elena Rostova", "elena@europe.eu", "+44207946099",
                "Password123!", "UK", CustomerType.FOREIGNER, IdentityType.PASSPORT,
                "P90002", IdentityStatus.VERIFIED, BigDecimal.valueOf(10000.00)));

        // Seed Admin User
        userRepository.save(new User("admin_1", "Platform Administrator", "admin@easyservice.com", "+251911999999",
                "AdminPass123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA,
                "FY000000", IdentityStatus.VERIFIED, BigDecimal.valueOf(100000.00)));

        // Seed Listings with authentic Ethiopian spots and host names
        // Hotels
        listingRepository.save(new Listing("list_1", "prov_1", "Hilton Presidential Suite", ListingCategory.HOTEL,
                "5-star luxury accommodation in central Kazanchis with full spa, mountain view & lounge.", BigDecimal.valueOf(1800.00), 5, 5, ListingStatus.PUBLISHED,
                "Kazanchis, Addis Ababa", "Bethlehem K."));

        listingRepository.save(new Listing("list_2", "prov_1", "Kuriftu Bishoftu Lakefront Villa", ListingCategory.HOTEL,
                "Serene lakefront resort villa in Bishoftu with private water garden and complimentary breakfast.", BigDecimal.valueOf(3200.00), 3, 3, ListingStatus.PUBLISHED,
                "Bishoftu Lakefront", "Tadesse M."));

        listingRepository.save(new Listing("list_3", "prov_1", "Haile Resort Hawassa Deluxe Room", ListingCategory.HOTEL,
                "Lake view luxury hotel room with complimentary swimming pool access and traditional dining.", BigDecimal.valueOf(1400.00), 8, 8, ListingStatus.PUBLISHED,
                "Lake Hawassa Waterfront", "Haile G."));

        // Car Rentals
        listingRepository.save(new Listing("list_4", "prov_2", "Toyota Land Cruiser V8 4x4", ListingCategory.CAR_RENTAL,
                "Off-road heavy duty luxury SUV with experienced local driver for countryside and safari tours.", BigDecimal.valueOf(2500.00), 4, 4, ListingStatus.PUBLISHED,
                "Bole Atlas, Addis Ababa", "Mesfin T."));

        listingRepository.save(new Listing("list_5", "prov_2", "Hyundai Elantra City Sedan", ListingCategory.CAR_RENTAL,
                "Fuel-efficient automatic sedan for smooth Addis Ababa city travel and airport transfer.", BigDecimal.valueOf(900.00), 6, 6, ListingStatus.PUBLISHED,
                "Meskel Square, Addis Ababa", "Dawit A."));

        listingRepository.save(new Listing("list_6", "prov_2", "Toyota Coaster VIP Tour Bus", ListingCategory.CAR_RENTAL,
                "25-seater executive bus with driver and AC for group travel, weddings, and corporate events.", BigDecimal.valueOf(4500.00), 2, 2, ListingStatus.PUBLISHED,
                "Piassa, Addis Ababa", "Getachew W."));

        // Events
        listingRepository.save(new Listing("list_7", "prov_3", "Addis Great Run VIP Pass", ListingCategory.EVENT,
                "Exclusive VIP access ticket and official t-shirt for the annual Great Ethiopian Run festival.", BigDecimal.valueOf(500.00), 50, 48, ListingStatus.PUBLISHED,
                "Jan Meda / Meskel Square", "Yared B."));

        listingRepository.save(new Listing("list_8", "prov_3", "Ethiopian New Year Cultural Gala", ListingCategory.EVENT,
                "Traditional dinner, live Azmari music performance, and coffee ceremony gala night.", BigDecimal.valueOf(1200.00), 30, 30, ListingStatus.PUBLISHED,
                "Skylight Hotel, Addis Ababa", "Azeb H."));

        listingRepository.save(new Listing("list_9", "prov_3", "African Jazz Summit Pass", ListingCategory.EVENT,
                "Two-day entrance pass to international Ethio-Jazz artist festival at Ghion Park.", BigDecimal.valueOf(800.00), 40, 40, ListingStatus.PUBLISHED,
                "Ghion Hotel Park, Addis Ababa", "Mulatu A."));

        // Store Products
        listingRepository.save(new Listing("list_10", "prov_4", "Handcrafted Jebena Coffee Set", ListingCategory.STORE,
                "Authentic 12-piece ceramic coffee set with hand-carved wooden Rekebot tray.", BigDecimal.valueOf(450.00), 15, 15, ListingStatus.PUBLISHED,
                "Shiro Meda, Addis Ababa", "Worknesh T."));

        listingRepository.save(new Listing("list_11", "prov_4", "Handwoven Habesha Kemis Dress", ListingCategory.STORE,
                "Premium traditional woven cotton dress with hand-embroidered gold Tibeb border.", BigDecimal.valueOf(3500.00), 5, 5, ListingStatus.PUBLISHED,
                "Entoto Cultural Village", "Meron S."));

        listingRepository.save(new Listing("list_12", "prov_4", "Yirgacheffe Specialty Coffee Beans (1kg)", ListingCategory.STORE,
                "Grade 1 single-origin freshly roasted Arabica coffee beans directly from Southern Ethiopian farmers.", BigDecimal.valueOf(380.00), 50, 50, ListingStatus.PUBLISHED,
                "Bole Coffee Hub, Addis Ababa", "Kifle D."));

        // Seed Promotions (BR-11, BR-12)
        promotionRepository.save(new Promotion("promo_1", null, "SUMMER20", 20.0,
                BigDecimal.valueOf(500.00), LocalDate.now().minusDays(5), LocalDate.now().plusDays(30), PromotionStatus.ACTIVE));

        promotionRepository.save(new Promotion("promo_2", null, "AAU10", 10.0,
                BigDecimal.valueOf(200.00), LocalDate.now().minusDays(5), LocalDate.now().plusDays(30), PromotionStatus.ACTIVE));

        System.out.println("✅ EasyService In-Memory Seeder: 10 Users, 1 Admin, 12 Detailed Ethiopian Listings, and Promotions initialized!");
    }
}
