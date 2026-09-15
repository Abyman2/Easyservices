# SOFTWARE TESTING & VALIDATION · FINAL PROJECT
## EasyService: Test Design Document — Part B (v1.0)

**Institution:** Addis Ababa University — School of Information Technology and Engineering  
**Course:** Software Testing and Validation  
**Document:** Test Design Document (Part B)  
**Repository:** https://github.com/Abyman2/Easyservice  

### Group Members
| # | Full Name | Student Number | Email Address | GitHub Username |
|---|---|---|---|---|
| 1 | **Abel Seleshe** | ATE/6743/14 | abelseleshi24@gmail.com | [Abyman2](https://github.com/Abyman2) |
| 2 | **Baheran Tesfaye** | ATE/5750/14 | Bahrantesfaye1@gmail.com | [Bahrann](https://github.com/Bahrann) |
| 3 | **Nebiyu Yohannes** | ATE/3973/14 | natijhonny@gmail.com | [NebiyuYohannes](https://github.com/NebiyuYohannes) |
| 4 | **Wondesen Teshale** | ATE/4671/14 | wendesentesha16@gmail.com | [WondesenTeshale](https://github.com/WondesenTeshale) |

---

## 1. Introduction & Traceability Matrix
This document details the formal test design derivations for EasyService based on Business Rules **BR-01 through BR-20**.

---

## 2. Equivalence Partitioning (EP)

### 2.1 Customer Identity & Registration (BR-01, BR-02)
- **Partition 1 (Valid Ethiopian)**: Ethiopian Customer + Valid Fayda National ID $\rightarrow$ `VERIFIED` (Valid)
- **Partition 2 (Invalid Ethiopian)**: Ethiopian Customer + Passport only $\rightarrow$ `REJECTED` (Invalid)
- **Partition 3 (Valid Foreigner)**: Foreign Customer + Valid Passport $\rightarrow$ `VERIFIED` (Valid)
- **Partition 4 (Invalid Foreigner)**: Foreign Customer + Fayda only $\rightarrow$ `REJECTED` (Invalid)

### 2.2 Payment Balance (BR-08, BR-09, BR-10)
- **Partition 1 (Insufficient)**: $\text{Balance} < \text{Amount}$ $\rightarrow$ Payment Declined (`EXPIRED` transaction)
- **Partition 2 (Exact Balance)**: $\text{Balance} = \text{Amount}$ $\rightarrow$ Payment Succeeded (`CONFIRMED` transaction)
- **Partition 3 (Sufficient Funds)**: $\text{Balance} > \text{Amount}$ $\rightarrow$ Payment Succeeded (`CONFIRMED` transaction)

---

## 3. Boundary Value Analysis (BVA)

### 3.1 Listing Capacity & Booking Quantity (BR-06, BR-07)
For a listing capacity $C = 100$:
- $Q = 0$: Invalid (Boundary below minimum quantity) $\rightarrow$ Rejected
- $Q = 1$: Valid (Minimum quantity boundary) $\rightarrow$ Accepted
- $Q = 99$: Valid (Nominal interior value) $\rightarrow$ Accepted
- $Q = 100$: Valid (Exact remaining capacity boundary) $\rightarrow$ Accepted
- $Q = 101$: Invalid (Boundary above maximum capacity) $\rightarrow$ Rejected

### 3.2 Promotion Minimum Threshold (BR-11)
For a promotion with minimum purchase requirement $M = 500.00$:
- $A = 499.99$: Invalid (Boundary below threshold) $\rightarrow$ Discount Rejected
- $A = 500.00$: Valid (Exact threshold boundary) $\rightarrow$ Discount Applied
- $A = 500.01$: Valid (Boundary above threshold) $\rightarrow$ Discount Applied

---

## 4. Decision Tables

### 4.1 Booking Eligibility Decision Table
| Conditions | Rule 1 | Rule 2 | Rule 3 | Rule 4 | Rule 5 | Rule 6 |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| Customer Identity Verified? | **Yes** | **No** | Yes | Yes | Yes | Yes |
| Listing Published? | **Yes** | — | **No** | Yes | Yes | Yes |
| Quantity $\le$ Available Stock? | **Yes** | — | — | **No** | Yes | Yes |
| Promo Code Valid? | **Yes** | — | — | — | **No** | Yes |
| Customer Balance $\ge$ Amount? | **Yes** | — | — | — | — | **No** |
| **Actions** | | | | | | |
| **Accept Booking (CONFIRMED)** | $\checkmark$ | | | | | |
| **Reject Booking (Error/EXPIRED)** | | $\checkmark$ | $\checkmark$ | $\checkmark$ | $\checkmark$ | $\checkmark$ |

---

## 5. State Transition Testing (Transaction State Machine)

```
        cancel                    cancel
    ┌──────────┐              ┌──────────┐
    ▼          │              ▼          │
CANCELLED  PENDING ──(pay)──> CONFIRMED ──(complete)──> COMPLETED
               │
           (expires)
               ▼
            EXPIRED
```

### 5.1 Valid State Transitions
- $T_1$: $\text{PENDING} \xrightarrow{\text{Payment Success}} \text{CONFIRMED}$
- $T_2$: $\text{CONFIRMED} \xrightarrow{\text{Service Complete}} \text{COMPLETED}$
- $T_3$: $\text{PENDING} \xrightarrow{\text{Cancel}} \text{CANCELLED}$
- $T_4$: $\text{CONFIRMED} \xrightarrow{\text{Cancel}} \text{CANCELLED}$ (Restores Inventory & Balance)
- $T_5$: $\text{PENDING} \xrightarrow{\text{Payment Failure}} \text{EXPIRED}$

### 5.2 Explicit Negative State Transition Tests
- $N_1$: $\text{COMPLETED} \xrightarrow{\text{Cancel}} \text{CANCELLED}$ $\rightarrow$ **FORBIDDEN (IllegalStateException)**
- $N_2$: $\text{COMPLETED} \xrightarrow{\text{Complete}} \text{COMPLETED}$ $\rightarrow$ **FORBIDDEN (IllegalStateException)**
- $N_3$: $\text{EXPIRED} \xrightarrow{\text{Confirm}} \text{CONFIRMED}$ $\rightarrow$ **FORBIDDEN (IllegalStateException)**
- $N_4$: $\text{CANCELLED} \xrightarrow{\text{Confirm}} \text{CONFIRMED}$ $\rightarrow$ **FORBIDDEN (IllegalStateException)**

---

## 6. Derived Automated Test Cases
1. `createBooking_Valid_Success()`: Verifies $T_1$ valid transition.
2. `createBooking_UnverifiedUser_ThrowsException()`: Verifies Decision Table Rule 2.
3. `createBooking_QuantityExceedsCapacity_ThrowsException()`: Verifies BVA boundary $Q = 101$.
4. `calculateDiscountedTotal_BVA_Threshold()`: Verifies BVA values ($499.99$, $500.00$, $500.01$).
5. `cancelTransaction_AlreadyCompleted_ThrowsException()`: Verifies Negative State Transition $N_1$.
