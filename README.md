# Kabka

*Kafka ka bhai* — a small distributed-messaging system inspired by Kafka, built from scratch in Java as a learning project. Topics, partitions, round-robin producers, and consumer groups with real committed offsets all work; there's no persistence, replication, or clustering (see [Limitations](#limitations)).

## Features

- Topics split into partitions, produce/consume over a REST API
- Round-robin partition selection when a producer doesn't pick one
- Consumer groups with server-tracked committed offsets (`poll` / `commit`), gated to each group's configured assignment
- Thread-safe partitions — verified under concurrent load, see [PERFORMANCE.md](PERFORMANCE.md)
- Latency, throughput, queue-depth, and consumer-lag metrics via Spring Boot Actuator + Micrometer
- Clean JSON error responses (404/400) instead of leaked stack traces

## Structure

Two Gradle modules:

- **`kabka-core`** — the messaging engine (plain Java, no web/Spring dependency)
- **`kabka-api`** — Spring Boot REST API that wraps `kabka-core`

## Requirements

- Java 25

## Run it

```bash
./gradlew :kabka-api:bootRun
```

Starts the API on `http://localhost:8080`. Run this in its own terminal — it blocks and streams logs.

Topics and consumer groups are seeded from [`application.yaml`](kabka-api/src/main/resources/application.yaml): `example-topic` (3 partitions), `another-topic` (5), `logs-topic` (4), all assigned to `consumer-group-1`.

Stop with `Ctrl+C`.

## API reference

| Method | Path | Params | Description |
|---|---|---|---|
| GET | `/api/admin/health` | — | Health check |
| POST | `/api/messages/push` | `topic`, `partition` *(optional)*, `message` | Produce a message; round-robins across partitions if `partition` is omitted |
| GET | `/api/messages/pull` | `topic`, `partition`, `offset`, `batchSize` | Raw read at an explicit offset — no consumer-group semantics |
| GET | `/api/messages/poll` | `topic`, `partition`, `group`, `batchSize` | Read from a consumer group's last committed offset (doesn't advance it) |
| POST | `/api/messages/commit` | `topic`, `partition`, `group`, `offset` | Commit a consumer group's offset |
| GET | `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus` | — | Metrics (see below) |

## Try it

```bash
curl -X POST "http://localhost:8080/api/messages/push" \
  --data-urlencode "topic=example-topic" --data-urlencode "message=Hello Kabka"
```

```bash
curl "http://localhost:8080/api/messages/poll?topic=example-topic&partition=0&group=consumer-group-1&batchSize=10"
```

```bash
curl -X POST "http://localhost:8080/api/messages/commit?topic=example-topic&partition=0&group=consumer-group-1&offset=1"
```

## Metrics

Actuator + Micrometer are on by default:

```bash
curl -s http://localhost:8080/actuator/prometheus | grep ^kabka_
```

To turn them off for a run instead of editing `application.yaml`:

```bash
# hide the endpoints only (metrics still collected internally)
./gradlew :kabka-api:bootRun --args='--management.endpoints.web.exposure.include=health'

# stop collecting Kabka's own metrics (Spring's built-in HTTP/JVM metrics stay on)
./gradlew :kabka-api:bootRun --args='--management.metrics.enable.kabka=false'
```

Both require a restart — these are read once at startup, there's no live toggle.

See [PERFORMANCE.md](PERFORMANCE.md) for latency/throughput reference numbers (local-machine only, not a benchmark).

## Tests

```bash
./gradlew test                    # all tests
./gradlew :kabka-core:test        # core module only
./gradlew :kabka-api:test         # api module only
```

## Other commands

```bash
./gradlew clean build             # full rebuild (rarely needed during dev)
```

## Limitations

- **In-memory only** — no disk persistence; a restart wipes every topic.
- **Single node** — no replication, no leader election, no clustering.
- **Static topology** — topics and consumer groups come from `application.yaml` at boot; no create/delete-topic API yet.
- **No message keys** — payload, offset, and timestamp only.
