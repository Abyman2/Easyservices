# EASYSERVICE MARKETPLACE — UNIFIED BOOKING ARCHITECTURE & DOMAIN MODEL SPECIFICATION

**Course:** Software Testing and Quality Assurance / Software Architecture  
**Institution:** Addis Ababa University — School of Information Technology and Engineering  
**Document Type:** Domain Model Specification & Unified Booking Engine Architecture  

---

## 1. CENTRAL ARCHITECTURE PHILOSOPHY

> **"A provider owns a business/listing, and that listing contains bookable units, options, or inventory."**

EasyService enforces a unified domain model where a single booking engine operates across four primary service categories (**Hotels & Stays**, **Drive & Car Rentals**, **Experiences & Events**, **Shop & Authentic Crafts**) without duplicating backend services.

```text
                                  PROVIDER
                                     │
                        ┌────────────┴────────────┐
                        │  BUSINESS / LISTING     │
                        ├─────────────────────────┤
                        │ • Name & Host           │
                        │ • Category & Location   │
                        │ • Description & Images  │
                        │ • Amenities & Policies  │
                        └────────────┬────────────┘
                                     │
                     ┌───────────────┴───────────────┐
                     │   BOOKABLE UNITS / ITEMS     │
                     ├───────────────────────────────┤
                     │ • Unit Name & Specs           │
                     │ • Capacity / Max Guests       │
                     │ • Unit Price (Per Night/Day)  │
                     │ • Driver Option (Cars)        │
                     │ • Total & Available Units     │
                     └───────────────┬───────────────┘
                                     │
                             CUSTOMER BOOKING
```

---

## 2. UNIFIED DOMAIN MODELS

### 2.1 Provider Business Model (`BusinessListing`)
```json
{
  "id": "prov_kuriftu_1",
  "name": "Kuriftu Resort & Spa Bishoftu",
  "category": "HOTEL",
  "location": "Bishoftu Lakefront, Oromia",
  "hostName": "Kuriftu Hospitality Group",
  "description": "Serene luxury lakefront resort offering spa wellness, fine dining, private pool villas, and lush gardens.",
  "rating": 4.8,
  "reviewCount": 240,
  "amenities": ["Free WiFi", "Swimming Pool", "Spa & Wellness", "Lakefront Dining", "Airport Shuttle"],
  "policies": {
    "checkInTime": "14:00",
    "checkOutTime": "11:00",
    "cancellation": "Free cancellation up to 24h before check-in"
  }
}
```

### 2.2 Category Bookable Units Specifications

#### A. 🏨 HOTEL (Stays & Accommodation)
```json
{
  "unitId": "unit_k1",
  "name": "Standard Room",
  "maxGuests": 2,
  "bedType": "1 Double Bed",
  "roomSize": "28 m²",
  "pricePerNight": 3500,
  "availableUnits": 8,
  "amenities": ["WiFi", "Breakfast Included", "Garden View"]
},
{
  "unitId": "unit_k2",
  "name": "Deluxe King Room",
  "maxGuests": 2,
  "bedType": "1 King Bed",
  "roomSize": "35 m²",
  "pricePerNight": 5000,
  "availableUnits": 4,
  "amenities": ["WiFi", "Jacuzzi", "Lake View", "Breakfast Included"]
},
{
  "unitId": "unit_k3",
  "name": "Family Villa Suite",
  "maxGuests": 5,
  "bedType": "2 Bedrooms (1 King + 2 Singles)",
  "roomSize": "85 m²",
  "pricePerNight": 10000,
  "availableUnits": 2,
  "amenities": ["Private Pool", "Full Kitchen", "Butler Service"]
}
```

#### B. 🚗 CAR RENTAL (Drive & Vehicle Fleets)
Includes the **Driver Choice Option** (`withDriver: true (+500 ETB/day)` or `selfDrive: true`):
```json
{
  "unitId": "unit_c1",
  "name": "Toyota Land Cruiser V8 4×4",
  "year": 2024,
  "transmission": "Automatic",
  "seats": 7,
  "fuelType": "Diesel",
  "pricePerDay": 3500,
  "driverFeePerDay": 500,
  "driverOptionAvailable": true,
  "availableUnits": 3,
  "badge": "4x4 Off-Road"
},
{
  "unitId": "unit_c2",
  "name": "Hyundai Tucson SUV",
  "year": 2023,
  "transmission": "Automatic",
  "seats": 5,
  "fuelType": "Petrol",
  "pricePerDay": 2500,
  "driverFeePerDay": 500,
  "driverOptionAvailable": true,
  "availableUnits": 5,
  "badge": "City SUV"
}
```

#### C. 🎟 EXPERIENCES & EVENTS (Ticket Tiers)
```json
{
  "unitId": "unit_e1",
  "name": "Regular Entrance Pass",
  "price": 500,
  "availableQuantity": 150,
  "perks": ["Main Stage Access", "Food Court Area"]
},
{
  "unitId": "unit_e2",
  "name": "VIP Elevated Pass",
  "price": 1500,
  "availableQuantity": 30,
  "perks": ["VIP Lounge", "Free Drinks", "Elevated Stage View"]
},
{
  "unitId": "unit_e3",
  "name": "VVIP Artist Meet & Greet Pass",
  "price": 3000,
  "availableQuantity": 10,
  "perks": ["Backstage Access", "Artist Meet & Greet", "Dinner Buffet"]
}
```

#### D. 🛍 SHOP & CRAFTS (Inventory Products)
```json
{
  "unitId": "unit_s1",
  "name": "Authentic Ceramic Jebena Set",
  "price": 450,
  "stockQuantity": 20,
  "options": ["Standard Clay", "Decorated Gold Border"]
},
{
  "unitId": "unit_s2",
  "name": "Handwoven Silk Habesha Kemis",
  "price": 3200,
  "stockQuantity": 8,
  "options": ["Size M", "Size L", "Size XL"]
}
```

---

## 3. UNIFIED PRICING ENGINE CALCULATIONS

The core `PricingService` executes category-specific subtotal logic:

$$\text{Hotel Subtotal} = \text{pricePerNight} \times \text{nights} \times \text{rooms}$$

$$\text{Car Subtotal} = (\text{pricePerDay} + (\text{hasDriver} ? 500 : 0)) \times \text{days} \times \text{vehicles}$$

$$\text{Event Subtotal} = \text{ticketPrice} \times \text{tickets}$$

$$\text{Store Subtotal} = \text{productPrice} \times \text{quantity}$$

$$\text{Final Total} = \text{Subtotal} - \text{Discount} + \text{Tax (15\% VAT)}$$

---

## 4. BOUNDARY VALUE ANALYSIS (BVA) TEST MATRICES

| Category | Test Case Parameter | Boundary Values | Expected Result | System Action |
|---|---|---|---|---|
| **Hotel** | Stay Duration (Nights) | `0 nights` | ❌ Invalid | Error: Minimum stay is 1 night |
| **Hotel** | Stay Duration (Nights) | `1 night`, `2 nights`, `30 nights` | ✅ Valid | Calculates subtotal correctly |
| **Hotel** | Room Stock Depletion | `Booking N = Stock` | ✅ Valid | Units remaining becomes 0 (Sold Out) |
| **Hotel** | Overbooking Guard | `Booking N > Stock` | ❌ Blocked | Reject transaction with Concurrency Lock |
| **Car Rental** | Driver Selection | `Self-Drive` vs `With Driver (+500)` | ✅ Valid | Adds 500 ETB/day fee to subtotal |
| **Car Rental** | Dates Validity | `Return Date < Pickup Date` | ❌ Invalid | Error: Invalid date range |
| **Event** | Ticket Order | `Quantity = 0` | ❌ Invalid | Error: Minimum 1 ticket required |
| **Event** | Ticket Order | `Quantity = Remaining Capacity` | ✅ Valid | Ticket stock successfully decremented |

---

## 5. SUMMARY

This unified architecture guarantees that EasyService scales across diverse service domains with a single, highly testable backend booking engine and a consistent customer user interface.
