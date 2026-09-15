# EasyService Discovery — Phase 3B

Phase 3B upgrades the Addis discovery service from a curated-only catalog to a hybrid
**live public-source discovery + verified fallback catalog**.

## What changed

- Java/Spring Boot 3.5.16, Java 21 remains unchanged.
- Real public HTTP discovery uses Jsoup.
- JSON-LD / Schema.org is preferred.
- Open public source pages are configured in `src/main/resources/addis-discovery-sources.json`.
- Robots.txt is checked before fetching a configured URL.
- Source failures are isolated; one blocked source cannot crash discovery.
- Location filtering supports `ALL`/`*` for city-wide search and named Addis areas.
- Results are deduplicated by normalized name + website + address.
- Missing phone, image and price values remain missing.
- Published prices keep their source currency.
- Test mode remains configurable at 5 results/category.
- The verified Phase-3 catalog remains a fallback so development is deterministic.
- `GET /api/discovery/addis/sources` exposes the active source configuration.

## Important

This is not a blind internet scraper. Only explicitly configured sources are crawled.
Operators must review source terms, robots rules and applicable law before enabling a source.
The service does not invent prices, phone numbers or images.

## Run

From this directory on Windows:

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

The service runs on port 8090.

## Test

```powershell
'{"city":"Addis Ababa","area":"ALL","category":"HOTEL"}' | Set-Content -Encoding utf8 test-discovery.json
curl.exe -X POST "http://localhost:8090/api/discovery/addis/search" -H "Content-Type: application/json" --data-binary "@test-discovery.json"
```

Repeat with `CAR`, `STORE`, and `EVENT`.

## Configuration

```properties
easyservice.discovery.live-enabled=true
easyservice.discovery.test-mode=true
easyservice.discovery.test-limit-per-category=5
easyservice.discovery.http-timeout-ms=12000
```

For a broader production-style run, set:

```properties
easyservice.discovery.test-mode=false
```

Do not turn off the test limit until the configured source set has been reviewed and tested.

## Architecture

Public Internet
-> EasyService Discovery :8090
-> Jsoup / structured-data extraction
-> normalization + location matching + deduplication
-> existing EasyService backend :8080
-> Svelte frontend :5173

The discovery service remains separate from the backend and does not add a database.
