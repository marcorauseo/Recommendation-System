# Video Recommendation Service

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
|---------|---------|---------------------|
| Edge routing & cross‑cutting concerns | **API Gateway / BFF** | All external traffic flows through the company API‑Gateway, which authenticates, authorizes, rate‑limits and routes requests to service instances discovered via Kubernetes. |
| Service decomposition | **Decompose by business capability & sub‑domain** | Recommendation logic is isolated from Catalog, User, and Playback services, enabling independent scaling and deployments. |
| Communication resilience | **Circuit Breaker** | Calls to external services (User, Catalog) use Resilience4j circuit breakers with 5‑second timeouts and exponential back‑off. |
| Data ownership | **Database per Service** | Each service (Recommendation, User, Catalog, Billing…) owns its schema; direct cross‑service SQL joins are prohibited. |
| Cross‑service consistency | **Saga (Choreography)** | User‑rating transactions emit `RatingCreated` events that trigger recommendation updates; compensations handled with an Outbox‑poller. |
| Query aggregation | **API Composition & CQRS** | Read‑optimised Redis cache is built from Kafka streams; complex joins performed by a lightweight API‑Composer in‑memory. |
| Isolation & performance | **Bulkhead** | Dedicated thread‑pools per outbound dependency; execution timeouts enforced. |
| Observability | **Health Check, Distributed Tracing, Structured Logging** | Spring Boot Actuator, OpenTelemetry OTLP exporter; logs shipped to Loki via Fluent Bit. |
| Developer productivity | **Microservice Chassis** | Shared Spring Boot starter provides base Dockerfile, Helm chart & plugins. |

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
          PostgreSQL    Other services
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

## Configuration (12‑Factor compliant)
| ENV | Default | Description |
|-----|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/reco` | Postgres JDBC URL |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Kafka brokers |
| `REDIS_URL` | `redis://redis:6379` | Redis connection |

Secrets should be provided via **Kubernetes Secrets** or **Docker secrets**.

## API (OpenAPI 3.1 excerpt)
```yaml
paths:
  /v1/users/{userId}/recommendations:
    get:
      summary: Top‑N recommendations for a user
  /v1/events/rating:
    post:
      summary: Submit a rating event
  /v1/events/playback:
    post:
      summary: Submit a playback progress event
```
Full spec: `openapi/recommendation.yaml`.

## Data model (h2)
| Table | Purpose |
|-------|---------|
| `rating_event` | user ratings (⭐1‑5) |
| `play_event` | playback positions |
| `recommendation` | denormalised top‑N list |

## Testing strategy
jacoco plug in, build failure if coverage under 60%
* **Unit**: JUnit5 + Mockito
* **Contract**: Spring Cloud Contract
* **Integration**: Testcontainers (PostgreSQL, Kafka, Redis)
* **E2E**: Karate DSL scenarios run against the Docker Compose stack

## CI/CD pipeline
GitHub Actions → Build → Unit & Int tests → Snyk scan → Docker build → Helm package → ArgoCD promotion (dev → staging → prod).

## Future work
* gRPC streaming endpoint for real‑time updates
* Blue/Green + Canary via Flagger & Prometheus SLOs
* Event sourcing & snapshotting of recommendation graphs
