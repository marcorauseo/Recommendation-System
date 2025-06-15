# Video Recommendation Service

## Future work

-Sonar analysis and issue resolution

-Increase JUnit coverage (at least 80 %)

Metrics & tracing: implement Grafana, Prometheus, and a MeterRegistry to expose
• JVM, Tomcat, Kafka-client metrics, plus custom counters & timers
• Kafka consumer lag, JVM heap usage, DB-connection utilisation
• One-line log entries enriched with service name, timestamp, and log level—ready for log-analytics tools

-Spring Security + JWT mock (basic auth at minimum) to decouple real user IDs

-Circuit breaker and resilience

-Redis cache (to avoid continuous DB calls)

## How to test
The entire application can be tested with Docker.

The docker-compose file spins up 4 containers.

From a bash prompt in the project root:

docker compose up --build

(you can also use docker plug in directly in intellij ide)

Make sure all containers start successfully.

Then, in your browser:

Swagger UI (for testing synchronous calls):
http://localhost:8080/swagger-ui/

Kafdrop console (for testing asynchronous events and creating Kafka topics):
http://localhost:9000/

Example – Add Message:

key: event_stream
value: {"userId":1,"movieId":1,"type":"RATING","rating":4}


In-memory database console (to inspect the database):
http://localhost:8080/h2-console/

The DB is filled at application start by the .csv in the data folder.

jdbc url:jdbc:h2:mem:recodb
user name: sa
pss:

Note: Because this app was built for a technical interview challenge, 
an in-memory database was chosen to simplify testing, since large data volumes are not expected.

## Test and debug without docker
If u want to just test synchronous call and debug in intellij you can use local.yml profile 
and comment kafka part in application.yml

spring-boot:run -Dspring-boot.run.profiles=local

## Purpose
This microservice generates **personalized movie & series recommendations** for each logged‑in user based on collaborative filtering and content similarity.

## Why a microservice?
Following the **Microservice Architecture** pattern, the Recommendation service is a small, autonomous component owned and deployed independently by the Personalization team.

## Patterns from Microservices.io applied

| Concern | Pattern | How it is addressed |
|-------|---------|--------------------|
|
| Service decomposition | **Decompose by business capability & sub‑domain** | Recommendation logic is isolated from Catalog, User, and Playback services, enabling independent scaling and deployments. |

| Data ownership | **Database per Service** | Each service (Recommendation, User, Catalog, Billing…) owns its schema; direct cross‑service SQL joins are prohibited. |
| Cross‑service consistency | **Saga (Choreography)** | User‑rating transactions emit `RatingCreated` events that trigger recommendation updates; |
| Query aggregation | **API Composition & CQRS**  |
| Isolation & performance | **Bulkhead** | Dedicated thread‑pools per outbound dependency; execution timeouts enforced. |
| Observability | **Health Check, Distributed Tracing, Structured Logging** | Spring Boot Actuator, OpenTelemetry OTLP exporter; logs shipped to Loki via Fluent Bit. |
| Developer productivity  | Shared Spring Boot starter provides base Dockerfile, Helm chart & plugins. |

## High‑level architecture
```
┌──────────┐     ┌──────────────┐
│  Client  │──▶ │  API Gateway  │
└──────────┘     └──────┬───────┘
                        │HTTP/JSON
        ┌───────────────▼───────────────┐
        │  Recommendation‑service       │
        │  (Spring Boot 3, Java 17)     │
        └────────┬───────────┬──────────┘
                 │Kafka      │HTTP
                 ▼           ▼
                 H2    Other services
                 │
                 ▼
               Redis (read model)
```

⚠️ **Prerequisite:** JDK17 is required to build and run this service 

## Build & Run locally
1. **Spin up infra**
   ```bash
   git clone https://github.com/marcorauseo/Recommendation-System
   cd recommendation-service
   make infra-up   # docker compose for Postgres, Kafka, Redis
   ```
2. **Start the service**
   ```bash
   ./mvnw spring-boot:run
   ```

## Container/Kubernetes
* Docker image published to `ghcr.io/contentwise/recommendation-service` on every merge to `main`.
* Helm chart located in `/helm/reco`, install with:
  ```bash
  helm repo add contentwise https://charts.contentwise.com
  helm install reco contentwise/recommendation -f values-dev.yaml
  ```





## API (OpenAPI 3.1 excerpt)
```yaml
openapi: 3.0.3
info:
  title: Recommendation Service API
  version: 1.0.0
  description: |
    REST interface for ingesting user interactions (rating/view events),
    searching the catalogue and retrieving personalised recommendations.
servers:
  - url: http://localhost:8080
paths:
  /v1/events:
    post:
      summary: Ingest a rating/view event (sync + push to Kafka)
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EventRequest'
      responses:
        "202": { description: Accepted }
  /v1/users/{userId}/events:
    get:
      summary: Fetch a user’s interaction history
      parameters:
        - $ref: '#/components/parameters/UserId'
        - in: query
          name: type
          schema:
            type: string
            enum: [rating, view]
          description: Filter by event type
        - $ref: '#/components/parameters/Page'
        - $ref: '#/components/parameters/Size'
      responses:
        "200":
          description: List of events
          content:
            application/json:
              schema:
                type: array
                items:
                  oneOf:
                    - $ref: '#/components/schemas/RatingEventDto'
                    - $ref: '#/components/schemas/ViewEventDto'
  /v1/movies:
    get:
      summary: Catalogue search
      parameters:
        - in: query
          name: title
          schema: { type: string }
          description: Full or partial title to match (case‑insensitive)
        - in: query
          name: genre
          schema:
            type: array
            items: { type: string }
          style: form
          explode: false
          description: One or more genres to match
        - in: query
          name: minRating
          schema: { type: number, format: double, minimum: 0, maximum: 5 }
        - in: query
          name: maxRating
          schema: { type: number, format: double, minimum: 0, maximum: 5 }
        - $ref: '#/components/parameters/Page'
        - $ref: '#/components/parameters/Size'
      responses:
        "200":
          description: Matching movies
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/MovieDto'
  /v1/users/{userId}/recommendations:
    get:
      summary: Personalised movie recommendations
      parameters:
        - $ref: '#/components/parameters/UserId'
        - in: query
          name: limit
          schema: { type: integer, minimum: 1, default: 10 }
      responses:
        "200":
          description: Ranked list of recommendations
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/RecommendationDto'
components:
  parameters:
    UserId:
      in: path
      name: userId
      required: true
      schema: { type: integer, minimum: 1 }
    Page:
      in: query
      name: offset
      schema: { type: integer, minimum: 0, default: 0 }
    Size:
      in: query
      name: limit
      schema: { type: integer, minimum: 1, default: 20 }
  schemas:
    EventRequest:
      type: object
      required: [userId, movieId, type]
      properties:
        userId:   { type: integer, minimum: 1 }
        movieId:  { type: integer, minimum: 1 }
        type:     { type: string, enum: [RATING, VIEW] }
        rating:   { type: integer, minimum: 1, maximum: 5 }
        viewPercent: { type: integer, minimum: 1, maximum: 100 }
    MovieDto:
      type: object
      properties:
        id:        { type: integer }
        title:     { type: string }
        genres:    { type: array, items: { type: string } }
        avgRating: { type: number, format: double }
    RatingEventDto:
      allOf:
        - $ref: '#/components/schemas/EventBase'
        - type: object
          properties:
            rating: { type: integer }
    ViewEventDto:
      allOf:
        - $ref: '#/components/schemas/EventBase'
        - type: object
          properties:
            viewPercent: { type: integer }
    EventBase:
      type: object
      properties:
        movieId: { type: integer }
        ts:      { type: string, format: date-time }
    RecommendationDto:
      type: object
      properties:
        movieId: { type: integer }
        score:   { type: number, format: double }
```
Full spec: `openapi/recommendation.yaml`.

## Data model (h2)
| Table | Purpose |
|-------|---------|
| `rating_event` | user ratings (⭐1‑5) |
| `play_event` | playback positions |
| `recommendation` | denormalised top‑N list |

# Data Logic – Recommendation Service

## Database Schema

| Table | Primary/Unique Keys | Columns (⚑ = FK) | Purpose |
| ----- | ------------------- |------------------| ------- |
|       |                     |                  |         |

| **APP\_USER**      | `id PK`                              | `username`                                                                               | Registered users.                                                   |
| ------------------ |--------------------------------------|------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| **MOVIE**          | `id PK`                              | `title`                                                                                  | Basic metadata for every movie.                                     |
| **MOVIE\_GENRE**   | `(movie_id⚑, genre)PK`               | –                                                                                        | Multi‑valued genres attached to each movie.                         |
| **RATING\_EVENT**  | `id PK`,`UNIQUE(user_id⚑,movie_id⚑)` | `user_id⚑`,`movie_id⚑`,`rating (1‑5)`, `source ENUM {EXPLICIT, IMPLICIT}`,`ts TIMESTAMP` | Explicit & implicit ratings (one per user‑movie).                   |
| **VIEW\_EVENT**    | `id PK`                              | `user_id⚑`,`movie_id⚑`,`view_percent INT`, `ts TIMESTAMP`                                | Captures watch‑time based implicit feedback. Multiple rows allowed. |
| **RECOMMENDATION** | `id PK`                              | `user_id⚑`,`movie_id`,`score DOUBLE`                                                     | Pre‑computed personalised list served by `/recommendations`.        |

### Relational Diagram (textual)

```
APP_USER 1 ────≻ RATING_EVENT ≺──── 1 MOVIE
   │                    │
   │                    ≻ VIEW_EVENT
   │
   ≻ RECOMMENDATION

MOVIE 1 ──≻ MOVIE_GENRE (0..n genres)
```

## Design Highlights

- **Normalised genres**: a join‑table (`MOVIE_GENRE`) keeps the schema flexible – new genres require no DDL.
- **Single rating rule**: `UNIQUE(user_id, movie_id)` on `RATING_EVENT` enforces "one rating per user per movie".
- **Explicit vs  Implicit**: `source` enum differentiates direct user ratings from algorithm‑derived ones (e.g. high view‑percent → stars).
- **Indices**
    - `RATING_EVENT (user_id, movie_id)` – recommendation queries.
    - `VIEW_EVENT (user_id, movie_id)` – implicit feedback lookup.
    - `MOVIE (title)` – add full‑text or trigram index for substring search.
- **Cascade deletes**: `ON DELETE CASCADE` on all FKs keeps interaction tables tidy when a user/movie is removed.
- **Sequences / IDENTITY**: every `id` is a `BIGINT GENERATED BY DEFAULT AS IDENTITY`.

## Query Patterns Covered

| Use‑case                                                     | SQL pattern                                                                             |
| ------------------------------------------------------------ |-----------------------------------------------------------------------------------------|
| **Search movies** by partial title, genre list, rating range | `SELECT … JOIN MOVIE_GENRE … aggregate AVG(rating)`                                     |
| **Recommendations** excluding seen movies                    | anti‑joins on `RATING_EVENT` & `VIEW_EVENT` then order by similarity/score              |
| **History feed**                                             | `SELECT * FROM RATING_EVENT WHERE user_id=? ORDER BY ts DESC` (& same for `VIEW_EVENT`) |

## Migration Strategy

- H2 uses the same DDL at integration‑test time.
- Production: flyway/liquibase script generated from this schema – supports PostgreSQL or Supabase.

---

*Last updated: 2025‑06‑15*




## Testing strategy
jacoco plug in, build failure if coverage under % defined in pom
* **Unit**: JUnit5 + Mockito
* **Contract**: Spring Cloud Contract
* **Integration**: Testcontainers (PostgreSQL, Kafka, Redis)
* **E2E**: Karate DSL scenarios run against the Docker Compose stack

## CI/CD pipeline
GitHub Actions → Build → Unit & Int tests → Snyk scan → Docker build → Helm package → ArgoCD promotion (dev → staging → prod).


