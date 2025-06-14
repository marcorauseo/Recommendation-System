# Video Recommendation Service

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

## Build & Run locally
1. **Spin up infra**
   ```bash
   git clone https://github.com/contentwise/recommendation-service.git
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

## Data model (PostgreSQL)
| Table | Purpose |
|-------|---------|
| `rating_event` | user ratings (⭐1‑5) |
| `play_event` | playback positions |
| `recommendation` | denormalised top‑N list |

## Testing strategy
* **Unit**: JUnit 5 + Mockito
* **Contract**: Spring Cloud Contract
* **Integration**: Testcontainers (PostgreSQL, Kafka, Redis)
* **E2E**: Karate DSL scenarios run against the Docker Compose stack

## CI/CD pipeline
GitHub Actions → Build → Unit & Int tests → Snyk scan → Docker build → Helm package → ArgoCD promotion (dev → staging → prod).

## Future work
* gRPC streaming endpoint for real‑time updates
* Blue/Green + Canary via Flagger & Prometheus SLOs
* Event sourcing & snapshotting of recommendation graphs
