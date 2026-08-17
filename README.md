# Kabka

*Kafka ka bhai* — a distributed messaging system inspired by Kafka, built from scratch in Java. Early-stage prototype: topics/partitions/consumer groups exist, but there's no persistence, offset tracking, or replication yet.

## Structure

Two Gradle modules:

- **`kabka-core`** — the messaging engine (plain Java, no web dependencies)
- **`kabka-api`** — Spring Boot REST API that wraps `kabka-core`

## Requirements

- Java 25

## Run it

```bash
./gradlew :kabka-api:bootRun
```

Starts the API on `http://localhost:8080`. Run this in its own terminal — it blocks and streams logs.

Available topics are seeded from [`application.yaml`](kabka-api/src/main/resources/application.yaml): `example-topic` (3 partitions), `another-topic` (5), `logs-topic` (4).

## Try it

In another terminal:

```bash
curl http://localhost:8080/api/admin/health
```

```bash
curl -X POST "http://localhost:8080/api/messages/push" \
  --data-urlencode "topic=example-topic" \
  --data-urlencode "partition=0" \
  --data-urlencode "message=Hello Kabka"
```

```bash
curl "http://localhost:8080/api/messages/pull?topic=example-topic&partition=0&offset=0&batchSize=10"
```

Stop the server with `Ctrl+C`.

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
