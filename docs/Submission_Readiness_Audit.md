# EasyService Submission Readiness Audit

**Review date:** 15 September 2026  
**Purpose:** Evidence-based check against the course brief and requested innovation-pitch deliverables.

## Executive verdict

The repository contains the application, a meaningful automated test suite, the required test documents, GitHub Actions, Jenkins configuration, and a working frontend build. It is **not yet fully compliant** with the brief as an evidence package.

The main blockers are:

1. The verified JaCoCo report now measures **84.7% branch coverage** for the core service package, and the Maven build enforces the 80% service-package gate. Whole-bundle coverage is 71.1% because it includes non-core classes.
2. Selenium previously returned from every test without executing. It now reports an explicit skip when the frontend is unavailable, and CI must run the frontend to obtain real E2E evidence.
3. The main testing documents now use the verified coverage result and current framework version; older historical PDFs may still need regeneration if they are submitted.
4. The regression demonstration is described, but a reproducible red-build artifact or commit reference is not stored in the repository.
5. The new pitch, business, SRS, feasibility, and readiness documents now have PDF exports in `docs/`.

## Rubric evidence matrix

| Requirement | Current evidence | Status | Required action |
|---|---|---:|---|
| Application with ranges/categories | Listing quantity, price, identity type, promotions | Met | Keep traceability in Part B |
| Conditional business logic | Identity, listing, availability, payment rules | Met | Add missing decision-table test rows |
| Stateful workflow | `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`, `EXPIRED` | Met | Ensure every transition has a test |
| Multi-step UI journey | Svelte booking flow plus Selenium class | Partial | Run Selenium against live frontend in CI |
| Test plan | `Test_Plan_Part_A.md` | Partial | Correct Spring version, roles, dates, and counts |
| Formal test design | `Test_Design_Part_B.md` | Partial | Add all derived cases and align expected outcomes with code |
| Unit tests | JUnit service and repository tests | Met | Add branch-focused tests |
| Test double | Fakes and spy implementations | Met | Keep examples linked to test methods |
| Integration tests | Spring Boot and MockMvc tests | Partial | Add endpoint coverage beyond one booking path |
| Selenium + Page Object | Page classes exist; E2E class currently uses direct locators | Partial | Refactor scenarios to call page objects |
| Statement and branch coverage | JaCoCo report and plugin | Met for core package | Service package is 84.7%; Maven enforces 0.80 |
| GitHub Actions | `.github/workflows/ci.yml` | Partial | Start frontend and retain npm cache/dependency steps |
| Jenkins | `Jenkinsfile` and Docker Compose | Ready to run | Configure JDK21, Maven3, and Node20 tools, then archive reports |
| Regression demonstration | Narrative in Part E | Partial | Store a reproducible before/after record or CI URLs |
| Defect log | `Defect_Log_And_Metrics_Part_FG.md` | Partial | Add reproduction, expected/actual, lifecycle dates, and escape policy |
| Metrics | DRE and defect counts | Partial | Recalculate from verified run and label escaped defects honestly |
| Summary and reflection | `Test_Summary_Report_Part_HI.md` | Met | Update release recommendation to reflect coverage gap |
| README | Root README | Partial | Link every document and state actual limitations |
| Group identity | Names and student IDs included | Met | Verify consent and contribution history before submission |
| Pitch deck | Existing technical presentation | Partial | Add investor/innovation narrative and ask |
| Business plan | Markdown and PDF | Met | `Business_Plan_EasyService.md/.pdf` |
| SRS | Markdown and PDF | Met | `Software_Requirements_Specification_EasyService.md/.pdf` |
| Financial feasibility | Markdown and PDF | Met | `Financial_Feasibility_Analysis.md/.pdf` |

## Verification commands

```powershell
cd backend
./mvnw.cmd clean test
./mvnw.cmd jacoco:check
./mvnw.cmd jacoco:report
cd ../frontend
npm install
npm run build
```

For real system tests, start the frontend first:

```powershell
cd frontend
npm run dev -- --host 127.0.0.1
```

Then run the Selenium class from a second terminal. A skipped Selenium test is an environment limitation, not passing functional evidence.

## Submission decision

**Current decision: conditional release for demonstration, not final academic submission.** The project is structurally strong, but the team should not claim full rubric compliance until coverage, live Selenium execution, CI evidence, and PDF export are completed and attached.

The coverage and PDF conditions are now complete. The remaining submission evidence is a successful hosted GitHub Actions run and a successful Jenkins run, plus a stored red/green regression build reference.
