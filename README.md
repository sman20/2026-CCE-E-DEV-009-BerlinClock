# Berlin Clock converter

Convert Digital Time into Berlin Clock Time and decode a valid reading back to Digital Time. 
The React interface has two independent forms and uses the Java/Spring API for every conversion, including its initial examples. 
Results are text, with selectable, scrollable output and clipboard controls.

## Prerequisites

- JDK 21, with `JAVA_HOME` pointing to that JDK.
- Installed **Maven 3.9.x** on `PATH`.
- Node.js **24.19.0** and npm. `frontend/.nvmrc` records the Node version.

Spring Boot **4.1.1**, React **19.3.0**, and supporting dependencies are pinned. 
Use the installed Maven 3.9.x distribution to build and run the backend. 
JUnit versions are managed by Spring Boot.

## Build and verify from a clean checkout

```sh
cd <cloned_repository>/backend
mvn verify
cd ../frontend
npm ci
npm run verify
```

`mvn verify` runs backend tests and packages the Spring Boot application. `npm run verify` runs frontend tests, ESLint, and the production build. 
`build` runs TypeScript checking before Vite, so verification checks types exactly once. 
GitHub Actions is configured to run these same checks using Java 21, runner-provided Maven, and Node 24.19.0.

Individual frontend commands:

```sh
npm run test
npm run typecheck
npm run lint
npm run build
```

## Run locally

Use two terminals, starting from the repository root.

Terminal 1:

```sh
cd backend
mvn spring-boot:run
```

Terminal 2:

```sh
cd frontend
npm ci
npm run dev
```

Open **http://localhost:5173**. The backend listens on **8080**; Vite proxies `/api` to `http://localhost:8080`.

## Input and API contracts

Input is validated as supplied after normal HTTP query decoding. Neither application layer trims, uppercases, repairs, truncates, or inserts separators.

**Digital Time** is exactly `HH:mm:ss`, with two ASCII digits per part, hours 00–23, and minutes/seconds 00–59. `12:32`, fractional seconds, `24:00:00`, leap seconds, whitespace, non-ASCII digits, and trailing line breaks are invalid.

**Berlin Clock Time** is exactly 24 uppercase letters: `Y` for yellow, `R` for red, and the letter `O` for off. The five components are:

| Component | Length | Illuminated pattern |
| --- | ---: | --- |
| Seconds Lamp | 1 | `Y` |
| Five Hours Row | 4 | `RRRR` |
| Single Hours Row | 4 | `RRRR` |
| Five Minutes Row | 11 | `YYRYYRYYRYY` |
| Single Minutes Row | 4 | `YYYY` |

Each row contains an illuminated prefix followed by off Lamps, with its prescribed colors. 
Spaces, wrong colors, lowercase, and combined hours above 23 are rejected.

Both endpoints return 200 and `application/json` with only their documented field:

```sh
curl --get 'http://localhost:8080/api/berlin-clock-time' \
  --data-urlencode 'digitalTime=13:17:01'
# {"berlinClockTime":"ORROORRROYYROOOOOOOOYYOO"}

curl --get 'http://localhost:8080/api/digital-time' \
  --data-urlencode 'berlinClockTime=ORROORRROYYROOOOOOOOYYOO'
# {"digitalTime":"13:17:01"}
```

## Implementation decisions and assessment deviations

The Seconds Lamp retains parity, so a reading cannot recover its original second. Reverse conversion selects the earliest consistent second: **`Y → 00`, `O → 01`**. 
It preserves hours and minutes and uses no original-time memory, current clock, or special cases. 
This differs from two published reverse examples:

| Berlin Clock Time | Published output | Implemented output |
| --- | --- | --- |
| `YOOOOOOOOOOOOOOOOOOOOOOO` | `00:00:00` | `00:00:00` |
| `ORRRRRRROYYRYYRYYRYYYYYY` | `23:59:59` | `23:59:01` |
| `YRRROROOOYYRYYRYYRYOOOOO` | `16:50:06` | `16:50:00` |
| `ORROOROOOYYRYYRYOOOOYYOO` | `11:37:01` | `11:37:01` |

[ADR 0001](docs/adr/0001-berlin-clock-time-to-digital-time-uses-earliest-consistent-second.md) explains the choice. 
All published forward examples are retained exactly. The design's reverse sample returns `13:17:01`, correcting its prototype's inverted seconds policy.

The [glossary](CONTEXT.md) defines the domain language.
