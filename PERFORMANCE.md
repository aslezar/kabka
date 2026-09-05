# Performance

## Disclaimer

These are informal, one-off numbers captured on a single developer laptop — **not** a rigorous benchmark. No warm-up runs, no isolation from other processes on the machine, small sample sizes, and the load generator (`curl` via `xargs`) shares the same CPU as the server under test. Treat these as "does this hold up at all" reference points, not a performance guarantee, an SLA, or a comparison against real Kafka.

## Environment

| | |
|---|---|
| Machine | Apple M4, 10 cores, 16 GB RAM |
| OS | macOS (Darwin) |
| Java | 25.0.1 (via Gradle toolchain) |
| Spring Boot | 4.0.1 |
| Storage | in-memory only (no disk I/O) |
| Captured | 2026-09-05 |

## Method

- Server started fresh via `./gradlew :kabka-api:bootRun`, default config (metrics on), no other load on the machine.
- Load generated with `xargs -P 50` firing concurrent `curl` requests from the same machine — there's no separate load-generator box, so the client competes with the server for CPU.
- Two different numbers below, measuring two different things:
  - **Engine latency** — read directly from Kabka's own Micrometer timers (`kabka.*.latency`) via `/actuator/prometheus`. This is time spent inside Kabka's own code only: no HTTP parsing, no network, no curl overhead.
  - **End-to-end throughput** — wall-clock time for N concurrent `curl` requests to complete. This is bottlenecked as much by spawning 50 local `curl` processes as by Kabka itself — a rough real-world reference, not Kabka's actual ceiling.

## Results

| Operation | Requests | Concurrency | Engine latency (avg / max) | End-to-end throughput |
|---|---|---|---|---|
| Produce (`POST /push`) | 1,000 | 50 | 0.09 ms / 2.71 ms | ~790 req/s |
| Consume (`GET /pull`) | 500 | 50 | 0.02 ms / 0.64 ms | ~980 req/s |
| Poll (`GET /poll`) | 500 | 50 | 0.02 ms / 1.05 ms | ~720 req/s |

Reproduce it yourself:

```bash
./gradlew :kabka-api:bootRun &
seq 1 1000 | xargs -P 50 -I{} curl -s -o /dev/null -X POST "localhost:8080/api/messages/push" \
  --data-urlencode "topic=example-topic" --data-urlencode "message=bench-{}"
curl -s localhost:8080/actuator/prometheus | grep kabka_produce_latency
```

## Correctness under load

Not a performance number, but worth noting alongside these: 100 concurrent pushes to a single partition via 20 parallel workers produced exactly 100 messages with contiguous, non-duplicated offsets — confirming the `synchronized` locking on `Partition` holds up under real concurrent writes rather than just looking fine in a single-threaded test.
