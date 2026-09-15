# EasyService 20-Slide Presentation Deck

## Slide 1: EasyService Architecture & Implementation Overview
**Cloud-Native Enterprise Marketplace Platform — Group Two**

- Course: Cloud Computing & Security (Microservices Architecture)
- Institution: Addis Ababa University — School of IT & Engineering
- Lead Contributor: Abel Seleshe (Architecture & Decomposition Lead)
- Team Members: Baheran Tesfaye, Nebiyu Yohannes, Wondesen Teshale, Esrom Basazinaw, Yohannes Seyum
- Target Implementation: Java 21, Spring Boot 3.2, Svelte Frontend & Docker/K8s

---

## Slide 2: Executive Summary & System Vision
**Transforming Monolithic Service Portals into Autonomous Microservices**

- Vision: Modern Ethiopian marketplace unifying Hotels, Mobility, Crafts, and Local Services
- Core Problem: Monolithic bottlenecks, shared database contention, and unified blast radiuses
- Architectural Solution: 6 Autonomous Bounded Context Microservices
- Security Foundation: Stateless OAuth2/JWT incorporating Fayda National Digital ID claims
- Key Capabilities: Reactive Wallet, Dynamic Spin-Wheel Engine & 1-Click Hot Deals Booking

---

## Slide 3: EasyService Monolith Decomposition
**Decomposing Legacy Systems into 6 Bounded Contexts**

- User-Service (Identity & Fayda/Passport Verification)
- Listing-Service (Marketplace Inventory & Multi-Room Hotel Units)
- Booking-Service (Atomic Inventory Locking & Booking Stepper State Engine)
- Payment-Service (EasyService Wallet Balance & Telebirr/CBE Transfers)
- Notification-Service (Real-time Event Alerts & Pre-Booking Reminders)
- Wheel-Promo-Service (Daily Spin Cost Limits & Unique Single-Use Promo Generator)

---

## Slide 4: Domain Model: Provider vs. Bookable Units
**Hierarchical Property Inventory Model**

- Root Provider/Business (e.g. Kuriftu Resort & Spa, Skylight Hotel, Habesha Kemis)
- Listing Metadata: Location, Amenities, Ratings, Photos & Policies
- Bookable Units / Options: Standard Room (3,500 ETB), Deluxe Room (5,000 ETB), Family Villa (10,000 ETB)
- Car Fleet Units: Toyota RAV4, Hyundai Elantra, Land Cruiser V8 (Daily Rates)
- Store Products: Yirgacheffe Coffee, Silk Kemis, Leather Briefcase (Item Units)

---

## Slide 5: Hotels & Accommodation Marketplace Engine
**Kuriftu, Skylight & Premium Resort Booking Flow**

- Discovery Page: Filter by Price, Star Rating, Property Type & Amenities
- Property Detail Page: Image Gallery, Location Coordinates & Full Specs
- Room Selection Matrix: Direct room selection with real-time room availability counts
- Date Pickers: Interactive Check-in & Check-out date range selection
- Nights Multiplier: Automated subtotal calculation based on stay duration

---

## Slide 6: Car Rental & Mobility Marketplace Engine
**Vehicle Fleet Specs & Rental Duration Engine**

- Fleet Inventory: SUV, Sedan, Luxury & Off-Road categories
- Vehicle Specs: Seats, Transmission, Fuel Type, Driver Option & Unlimited KM
- Rental Date Range: Pickup Date and Return Date selector
- Day Multiplier: Automatic rental duration calculation (Daily Rate × Days × Quantity)
- Instant Validation: Return date must strictly be after pickup date

---

## Slide 7: Cultural Products, Crafts & Events Portal
**Ethiopian Heritage Kemis, Leather Goods & Ticket Booking**

- Authentic Local Stores: Habesha Heritage Kemis, Entoto Spices, Axum Crafts
- Item Variants: Specific sizes, silk borders, and material specifications
- Concerts & Cultural Events: Great Ethiopian Run, Jan Meda Festival tickets
- Preferred Date Picker: Schedule delivery or event attendance date
- Direct Provider Settlement: Integrated commission split engine

---

## Slide 8: Customer Identity & Fayda Verification
**Zero-Trust Security & National ID Integration**

- AAU Academic Domain SSO: Restricted registration for verified student/faculty credentials
- Fayda National Digital ID Integration: Verification badge (FAYDA_VERIFIED)
- Passport Identity Status: Level-2 identity trust scores
- Stateless JWT Security: RSA-256 signed bearer tokens
- Role-Based Access Control: CUSTOMER, PROVIDER, SYSTEM_ADMIN roles

---

## Slide 9: Reactive EasyService Wallet System
**Real-time Balance Management & Atomic Deductions**

- Simulated Wallet Store: $currentUser reactive state management
- Instant Balance Check: Blocks transaction if balance < total payable
- Atomic Deduction: Mutates user balance instantly across UI navbar & passport
- Alternative Payment Channels: Integrated Telebirr, CBE Birr & Card options
- Instant Refund Engine: Wallet credits returned upon booking cancellation

---

## Slide 10: Spin-the-Wheel Promotion Engine
**Tiered Spin Costs & Unique Single-Use Promo Codes**

- Tiered Cost Rules: Spins 1-3 (FREE), Spins 4-9 (50 ETB), Spins 10-15 (100 ETB)
- Wallet Protection: Deducts spin cost directly from EasyService wallet balance
- Randomized Code Generator: Generates unique code per spin (e.g. WIN10-8492)
- Central Code Registry: Registers code in activePromoCodes store to prevent reuse
- Clipboard Copy Action: 1-Click copy button with text fallback

---

## Slide 11: Hot Deals & Dynamic Pricing Engine
**Pre-applied Promos & Flash Sale Bookings**

- Marketing Banner Integration: Direct 1-click 'Book Deal →' triggers
- Pre-applied Promo Deep Linking: Automatically loads promo codes (SUMMER20, ETHIO30)
- Dynamic Savings Calculation: Displays percentage discount & ETB amount saved
- Service Fee Inclusion: Transparent 5% platform service fee breakdown
- Real-time Subtotal Formula: (Unit Price × Quantity × Duration - Discount) + Fee

---

## Slide 12: End-to-End Booking Stepper Architecture
**5-Step Transactional Booking Flow**

- Step 1 (Select): Quantity, Dates (Check-in/out), and Promo Code input
- Step 2 (Details): Customer Name, Email, Phone & Identity Status verification
- Step 3 (Review): Comprehensive line-item summary and cancellation policy
- Step 4 (Payment): Payment method selection (Wallet, Telebirr, CBE) & charge execution
- Step 5 (Confirm): Green receipt badge, reference ID, schedule dates & wallet update

---

## Slide 13: Date Range Pickers & Schedule Calculation
**Native Date Controls & Duration Engine**

- Native HTML5 Date Pickers: Minimum date bounded to current day (getTodayStr)
- Date Difference Engine: Math.ceil((end - start) / 86,400,000)
- Category-Aware Labels: Check-in/out for Hotels, Pickup/Return for Cars
- Date Range Badge: Visual summary badge displaying computed duration
- Confirmed Schedule Payload: Persists dates to bookingResult and store history

---

## Slide 14: Microservices Technical Architecture
**Spring Boot 3.2, REST & Messaging Mesh**

- Framework: Java 21 LTS with Spring Boot 3.2 Cloud-Native stack
- API Gateway: Spring Cloud Gateway for routing, rate limiting & SSL termination
- Service Discovery: Netflix Eureka Service Registry
- Inter-Service Sync: RESTful HTTP/2 APIs & gRPC binary protocol
- Async Messaging: Apache Kafka event streams for notifications and Saga triggers

---

## Slide 15: Database Schema & Data Isolation
**Database per Service Pattern Implementation**

- Isolated Storage: Dedicated PostgreSQL schema per microservice boundary
- User DB: Customer profiles, credentials & identity verification flags
- Listing DB: Provider properties, room units, specs & inventory counters
- Booking DB: Reservation ledger, stepper status & timestamp audit logs
- Performance Indexing: B-Tree indexes on listing_id, customer_id & status

---

## Slide 16: Frontend Architecture & UX Design
**Svelte 4 Reactive Store & Custom CSS Token System**

- UI Framework: Svelte 4 compiler for zero-virtual-DOM performance
- State Management: Reactive stores (authStore.js, bookingStore.js)
- Visual Aesthetic: Modern dark theme, glassmorphism & gold accents (#d4af37)
- Component Library: Modular components (BookingModal, SpinWheelModal, Navbar)
- Accessibility (A11y): ARIA roles, high-contrast text & modal keyboard focus

---

## Slide 17: API Specifications & Endpoints
**OpenAPI 3.0 Standardized Service Contracts**

- GET /api/listings - Fetch 28 curated Ethiopian marketplace business listings
- POST /api/bookings?customerId=... - Execute atomic reservation transaction
- POST /api/wallet/topup - Reactive wallet credit deposit
- POST /api/spin/wheel - Execute promotional spin & register unique code
- Swagger UI: Live API documentation interactive sandbox at /swagger-ui.html

---

## Slide 18: Quality Assurance & Test Suite
**Testing Pyramid & Defect Metric Analysis**

- Unit Testing: JUnit 5 & Mockito testing core domain business rules
- Contract Testing: Pact framework verifying microservice API provider/consumer contracts
- Integration Testing: Testcontainers PostgreSQL & Kafka containerized integration tests
- End-to-End Testing: Playwright automated headless browser test flows
- Defect Zero Guarantee: Built-in fail-safe fallbacks for offline demo resiliency

---

## Slide 19: Deployment & CI/CD Pipeline
**Docker Containerization & Cloud Hosting Strategy**

- Containerization: Multi-stage Dockerfiles producing distroless Alpine images
- Orchestration: Kubernetes manifests (Deployment, Service, Ingress, HPA)
- Continuous Integration: GitHub Actions workflow building and testing images
- Cloud Deployment: Railway / Vercel containerized deployment
- Observability: Prometheus metrics scraper & Grafana dashboard visualizer

---

## Slide 20: Conclusion & Project Roadmap
**Summary of Accomplishments & Future Extensions**

- Accomplishment: Fully functional, hardened Ethiopian Service Marketplace platform
- Wallet Integrity: Reactive, fail-safe wallet payments and spin wheel charges
- Promo Engine: Unique single-use promo code generation and dynamic discount parsing
- Booking Flow: Seamless check-in/out date pickers and night multipliers
- Next Phase: Real-world Spring Boot backend REST integration & Telebirr Production API

---

