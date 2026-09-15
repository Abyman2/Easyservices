# EasyService Marketplace Platform — Final Testing & Validation Project

## Addis Ababa University
### School of Information Technology and Engineering
**Course:** Software Testing and Validation  
**Instructor:** Abel Tadesse  
**Deadline:** Sunday, 13 September 2026, end of day  

---

## 👥 Group Members
| # | Full Name | Student Number | Email Address | GitHub Username |
|---|---|---|---|---|
| 1 | **Abel Seleshe** | ATE/6743/14 | abelseleshi24@gmail.com | [@Abyman2](https://github.com/Abyman2) |
| 2 | **Baheran Tesfaye** | ATE/5750/14 | Bahrantesfaye1@gmail.com | [@Bahrann](https://github.com/Bahrann) |
| 3 | **Nebiyu Yohannes** | ATE/3973/14 | natijhonny@gmail.com | [@NebiyuYohannes](https://github.com/NebiyuYohannes) |
| 4 | **Wondesen Teshale** | ATE/4671/14 | wendesentesha16@gmail.com | [@WondesenTeshale](https://github.com/WondesenTeshale) |

---

## 📌 Project Overview
EasyService is an Ethiopia-first full-stack marketplace connecting customers with verified service providers across four categories: **Hotels, Car Rentals, Events, and Store Products**.

This project demonstrates a **complete, professional software testing effort** built around a unified marketplace engine ($\text{Listing} \rightarrow \text{Transaction} \rightarrow \text{Payment} \rightarrow \text{Transaction State}$), featuring:
1. **Formal Black-Box Test Design**: Equivalence Partitioning (EP), Boundary Value Analysis (BVA), Decision Tables, and Transaction State Machine Testing.
2. **Automated Test Pyramid**:
   - **Unit Tests**: JUnit 5 + Mockito covering pricing, identity rules, stock capacity, and state transitions.
   - **Integration Tests**: Spring Boot Test + MockMvc validating REST endpoints.
   - **System Tests**: Selenium WebDriver with the **Page Object Model (POM)** pattern.
   - **Test Doubles**: Fakes (`FakePaymentService`, `FakeIdentityVerificationService`), Mocks/Spies (`FakeNotificationService`), and Controlled Randomness (`FakeRandomNumberGenerator`).
3. **Code Coverage**: JaCoCo generates statement and branch reports and enforces **80% branch coverage on the core service package**. The verified service-package result is **84.7%** (171/202 branches); whole-bundle coverage remains lower because it includes controllers and infrastructure.
4. **Continuous Integration & Automation**: GitHub Actions workflow and Jenkins Docker pipeline.
5. **Defect Management & Quality Metrics**: Defect lifecycle tracking, DRE calculation (100%), and final release recommendation.

---

## 🚀 How to Run the Application & Test Suites

### Prerequisites
- **Java 21 (JDK)**
- **Apache Maven 3.8+**
- **Node.js 18+** (for frontend Svelte UI)
- **Docker & Docker Compose** (for local Jenkins server)

### 1. Build and Run Backend
```bash
cd backend
mvn spring-boot:run
```
The REST API will start at `http://localhost:8080`.

### 2. Run Automated Test Suite (Unit & Integration)
```bash
cd backend
mvn clean test
```

Selenium tests require Chrome and the frontend server. Start the frontend in another terminal for live browser evidence:

```bash
cd frontend
npm install
npm run dev -- --host 127.0.0.1
```

### 3. Generate JaCoCo Code Coverage Report
```bash
cd backend
mvn jacoco:report
```
View the generated HTML report at: `backend/target/site/jacoco/index.html`.

### 4. Verify JaCoCo Coverage Threshold
```bash
cd backend
mvn jacoco:check
```

The command fails if `com.easyservice.backend.service` is below 80% branch coverage. To inspect the exact result, open `backend/target/site/jacoco/index.html`, select the `com.easyservice.backend.service` package, and read the Branch column. The raw CSV is `backend/target/site/jacoco/jacoco.csv`.

---

## ⚙️ Continuous Integration Pipelines

### GitHub Actions
The GitHub Actions workflow is configured in [.github/workflows/ci.yml](.github/workflows/ci.yml). On every push or pull request:
- JDK 21 is set up.
- All unit and integration test suites are executed via Maven.
- JaCoCo checks the core service-package threshold and the interim bundle threshold.
- Test reports are uploaded as artifacts.

### Jenkins (via Docker)
To launch the self-hosted Jenkins server locally:
```bash
cd docker
docker-compose -f docker-compose-jenkins.yml up -d
```
Access Jenkins at `http://localhost:8080`. The pipeline definition is stored in [Jenkinsfile](Jenkinsfile).

---

## 📄 Deliverable PDF Source Documents (`docs/`)
All required submission documents are available in the `docs/` folder:
1. **Part A — Test Plan (v1.0)**: [docs/Test_Plan_Part_A.md](docs/Test_Plan_Part_A.md)
2. **Part B — Test Design Document**: [docs/Test_Design_Part_B.md](docs/Test_Design_Part_B.md)
3. **Part C — Automated Test Suite (Unit, Integration, System)**: [docs/Test_Automation_Part_C.md](docs/Test_Automation_Part_C.md)
4. **Part D — Code Coverage Report (JaCoCo)**: [docs/Code_Coverage_Part_D.md](docs/Code_Coverage_Part_D.md)
5. **Part E — CI Pipelines & Regression Demo (GitHub Actions + Jenkins)**: [docs/CI_Pipelines_Part_E.md](docs/CI_Pipelines_Part_E.md)
6. **Parts F & G — Defect Log & Quality Metrics**: [docs/Defect_Log_And_Metrics_Part_FG.md](docs/Defect_Log_And_Metrics_Part_FG.md)
7. **Parts H & I — Test Summary Report & Foundations Reflection**: [docs/Test_Summary_Report_Part_HI.md](docs/Test_Summary_Report_Part_HI.md)
8. **Submission Readiness Audit**: [docs/Submission_Readiness_Audit.md](docs/Submission_Readiness_Audit.md)
9. **Software Requirements Specification**: [docs/Software_Requirements_Specification_EasyService.md](docs/Software_Requirements_Specification_EasyService.md)
10. **Business Plan**: [docs/Business_Plan_EasyService.md](docs/Business_Plan_EasyService.md)
11. **Financial Feasibility Analysis**: [docs/Financial_Feasibility_Analysis.md](docs/Financial_Feasibility_Analysis.md)
12. **Addis Innovation Pitch Deck**: [docs/Investor_Pitch_Deck_Addis_Innovation.md](docs/Investor_Pitch_Deck_Addis_Innovation.md)
13. **Submission Readiness Audit PDF**: [docs/Submission_Readiness_Audit.pdf](docs/Submission_Readiness_Audit.pdf)
14. **Business Plan PDF**: [docs/Business_Plan_EasyService.pdf](docs/Business_Plan_EasyService.pdf)
15. **SRS PDF**: [docs/Software_Requirements_Specification_EasyService.pdf](docs/Software_Requirements_Specification_EasyService.pdf)
16. **Financial Feasibility PDF**: [docs/Financial_Feasibility_Analysis.pdf](docs/Financial_Feasibility_Analysis.pdf)
17. **Pitch Deck PDF**: [docs/Investor_Pitch_Deck_Addis_Innovation.pdf](docs/Investor_Pitch_Deck_Addis_Innovation.pdf)

---

## 🗺️ Concept Coverage Map (Section 8 Verification Checklist)
| Course Concept | Source Unit | Where Demonstrated in Project |
| :--- | :--- | :--- |
| **Error, Fault, Failure; Verification vs Validation** | Unit 1 | `docs/Test_Summary_Report_Part_HI.md` (Part I) & `docs/Defect_Log_And_Metrics_Part_FG.md` |
| **Test Design Techniques (EP, BVA, Decision Tables, State Transitions)** | Unit 2 | `docs/Test_Design_Part_B.md` (Part B) & `docs/Test_Automation_Part_C.md` (Part C, Section 3) |
| **Code Coverage: Statement and Branch** | Unit 2, 4 | `docs/Code_Coverage_Part_D.md` (Part D) & `backend/pom.xml` (`jacoco-maven-plugin`); the core service package passes the enforced 80% branch target |
| **Test Levels: Unit, Integration, System** | Unit 3 | `docs/Test_Automation_Part_C.md` (Part C, Sections 3–5) & `backend/src/test/java/` |
| **Test Doubles: Stub, Mock, Fake, Spy** | Unit 3 | `docs/Test_Automation_Part_C.md` (Part C, Section 2) & `infrastructure/Fake*.java` |
| **The Test Pyramid** | Unit 3, 4 | `docs/Test_Automation_Part_C.md` (Part C, Section 1) — 52 unit + 2 integration + 4 system methods |
| **Selenium & Page Object Pattern** | Unit 3 | `docs/Test_Automation_Part_C.md` (Part C, Section 5) & `selenium/pages/`; live execution requires the frontend server |
| **Regression Testing & Automation** | Unit 4 | `docs/CI_Pipelines_Part_E.md` (Part E, Section 3) — DEF-002 regression demo |
| **Continuous Integration (GitHub Actions & Jenkins)** | Unit 4 | `docs/CI_Pipelines_Part_E.md` (Part E) & `.github/workflows/ci.yml` & `Jenkinsfile` |
| **Test Management: Plan, Criteria, Risk** | Unit 5 | `docs/Test_Plan_Part_A.md` (Part A) |
| **Defect Management & Quality Metrics** | Unit 5 | `docs/Defect_Log_And_Metrics_Part_FG.md` (Parts F & G) |
| **Quality & The Stopping Decision** | Unit 5 | `docs/Test_Summary_Report_Part_HI.md` (Part H - Release Recommendation) |
