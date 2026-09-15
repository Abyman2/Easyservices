package com.easyservice.backend.controller;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.*;
import com.easyservice.backend.repository.ListingRepository;
import com.easyservice.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    private User verifiedCustomer;
    private Listing publishedListing;

    @BeforeEach
    void setUp() {
        userRepository.clear();
        listingRepository.clear();

        verifiedCustomer = new User("cust100", "Verified Customer", "cust100@aau.edu.et", "+251911999999",
                "Secret123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA,
                "FY999999", IdentityStatus.VERIFIED, BigDecimal.valueOf(2000.00));
        userRepository.save(verifiedCustomer);

        publishedListing = new Listing("list100", "prov100", "Car Rental Toyota", ListingCategory.CAR_RENTAL,
                "Sedan", BigDecimal.valueOf(500.00), 3, 3, ListingStatus.PUBLISHED);
        listingRepository.save(publishedListing);
    }

    @Test
    @DisplayName("REST Integration: POST /api/bookings creates booking transaction successfully")
    void createBooking_IntegrationSuccess() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .param("customerId", "cust100")
                        .param("listingId", "list100")
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(500.0));
    }
}
