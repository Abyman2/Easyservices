# EasyService Business Plan

## 1. Executive summary

EasyService is an Ethiopia-first digital marketplace for discovering and booking hotels, car rentals, events, and selected local products. The product reduces friction between customers and providers by combining verified identities, inventory-aware transactions, simulated wallet payment, promotions, and booking lifecycle management.

The current repository is an academic prototype. This plan describes a controlled path from prototype to Addis Ababa pilot and clearly separates validated product behavior from commercial assumptions.

## 2. Mission and objectives

**Mission:** Make trusted local services easy to discover and transact.

**Pilot objectives:**

- Onboard 25 providers across two initial categories.
- Enable 500 registered customer accounts.
- Complete 300 paid pilot transactions.
- Keep booking failure and reconciliation incidents below 2%.
- Collect provider and customer evidence before geographic expansion.

## 3. Customer segments

| Segment | Need | Initial offer |
|---|---|---|
| Local customers | Reliable discovery and availability | Search, booking, wallet, promotions |
| International visitors | Local services with clearer trust signals | Verified providers, English-first flow, support |
| Hospitality and event providers | More demand and less manual coordination | Listing tools, inventory, analytics |
| Local product sellers | Digital discovery and order capture | Curated catalogue and campaign placement |

## 4. Value proposition

Customers get one consistent flow from discovery to confirmation. Providers get a structured digital storefront and a transaction record. The platform creates trust through verification, explicit status transitions, inventory checks, and visible cancellation behavior.

## 5. Product and operating model

The current product supports registration, identity verification, listing search, booking, promotion, simulated payment, cancellation, inventory restoration, and provider-facing listing management.

A production operating model must add provider onboarding review, customer support, refund operations, fraud monitoring, service-level agreements, and incident response.

## 6. Revenue model

1. **Transaction commission:** percentage of completed booking value.
2. **Provider subscription:** optional monthly package for analytics and promoted placement.
3. **Sponsored discovery:** paid placement with a visible sponsored label.
4. **Convenience fee:** applied only where customer value and regulations support it.

## 7. Go-to-market

Start with a narrow Addis Ababa pilot focused on hotels and events, where inventory and booking value are easy to demonstrate. Recruit providers directly, use partner referrals, and measure completed transactions rather than registrations alone. Expand to car rentals and local products after the pilot proves reconciliation and support workflows.

## 8. Competitive position

The strongest position is not simply a larger catalogue. It is local operational fit: identity choices relevant to Ethiopia, transparent inventory, provider tools, and a testable transaction state machine. Trust, support quality, and supply density are the main defensible capabilities to build.

## 9. Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Low provider supply | High | Category-focused pilot and assisted onboarding |
| Payment or refund failure | High | Regulated gateway, ledger, reconciliation, manual fallback |
| Fraud or identity misuse | High | Verification review, rate limits, audit logs |
| Customer acquisition cost | Medium | Provider-led referrals and targeted partnerships |
| Coverage and reliability gaps | High | Raise core branch coverage, execute live E2E, monitor production |
| Regulatory uncertainty | High | Legal review before real-money launch |

## 10. Milestones

| Phase | Outcome |
|---|---|
| Prototype hardening | Persistent data, authentication, observability, CI evidence |
| Addis pilot | 25 providers and 300 completed transactions |
| Operational validation | Support, refunds, reconciliation, provider SLA |
| Category expansion | Car rentals and local products |
| Commercial launch | Regulated payments, privacy and security sign-off |

## 11. Success metrics

North-star metric: completed, successfully reconciled bookings per active provider.

Supporting metrics: conversion from listing view to booking, repeat customer rate, provider activation, cancellation rate, refund time, support resolution time, transaction margin, and escaped defect rate.

## 12. Funding use

Illustrative seed use should prioritize product hardening and trust infrastructure: engineering, regulated payments, security/privacy, provider acquisition, customer support, and pilot measurement. Exact amounts belong in the financial model and must be replaced with supplier quotations and approved staffing costs.

## 13. Recommendation

Proceed with a limited, non-production pilot after the technical evidence gaps are closed. Do not represent the current in-memory prototype as a production marketplace or process real customer funds.
