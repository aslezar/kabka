# Kabka Multi-Module Project Structure

## Overview

Kabka is a distributed messaging system (similar to Kafka) built with a **clean multi-module architecture**:

- **kabka-core**: Pure Java messaging engine (no web dependencies)
- **kabka-api**: Spring Boot REST API that exposes kabka-core functionality

## Project Structure

```
kabka/                                    # Root project
├── settings.gradle.kts                   # Declares the two modules
├── build.gradle.kts                      # Shared configuration
├── gradlew / gradlew.bat                 # Gradle wrapper scripts
│
├── kabka-core/                           # Module 1: Core Engine
│   ├── build.gradle.kts                  # Core dependencies (SLF4J, Logback)
│   └── src/
│       ├── main/java/dev/kabka/core/
│       │   ├── KabkaEngine.java          # Main engine entry point
│       │   ├── broker/
│       │   │   └── Broker.java           # Message broker logic
│       │   ├── storage/
│       │   │   └── MessageStore.java     # Persistence layer
│       │   └── replication/
│       │       └── ReplicationManager.java # Data replication
│       └── test/java/dev/kabka/core/
│           └── KabkaEngineTest.java      # Unit tests
│
└── kabka-api/                            # Module 2: REST API
    ├── build.gradle.kts                  # Depends on kabka-core + Spring Boot
    └── src/
        ├── main/java/dev/kabka/api/
        │   ├── KabkaApiApplication.java  # Spring Boot entry point
        │   ├── config/
        │   │   └── KabkaConfig.java      # Initializes KabkaEngine
        │   └── controller/
        │       ├── AdminController.java   # Admin endpoints (/api/admin/*)
        │       └── MessageController.java # Messaging endpoints (/api/messages/*)
        ├── resources/
        │   └── application.yaml          # Spring Boot config (port 8080)
        └── test/java/dev/kabka/api/
            └── KabkaApiApplicationTests.java
```

## Module Dependencies

```
kabka-api  →  kabka-core  →  JDK + SLF4J
    ↓
Spring Boot
```

- **kabka-core** is standalone (can be embedded in any Java app)
- **kabka-api** wraps kabka-core with HTTP/REST interface

## Build Commands

### Build Everything
```bash
./gradlew clean build
```

### Build Individual Modules
```bash
./gradlew :kabka-core:build
./gradlew :kabka-api:build
```

### Run the API
```bash
./gradlew :kabka-api:bootRun
```

API starts on: `http://localhost:8080`

### Run Tests
```bash
./gradlew test                    # All tests
./gradlew :kabka-core:test        # Core tests only
./gradlew :kabka-api:test         # API tests only
```

## API Endpoints

### Admin Endpoints
```bash
# Health Check
curl http://localhost:8080/api/admin/health

# Response: {"service":"Kabka Messaging System","status":"UP", "version":"1.0.0"}
```

### Message Endpoints
```bash
# Publish Message
curl -X POST http://localhost:8080/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{"topic":"test-topic","message":"Hello Kabka"}'

# Response: {"status":"published","topic":"test-topic","timestamp":"..."}
```

## Why This Structure?

### ✅ Separation of Concerns
- Core logic isolated from web layer
- Can add CLI, gRPC, WebSocket interfaces later without touching core

### ✅ Testability
- Test kabka-core independently (unit tests)
- Test kabka-api with Spring Boot Test (integration tests)

### ✅ Reusability
- Embed kabka-core in other applications
- Use kabka-api as a standalone microservice

### ✅ Clean Dependencies
- Core has minimal dependencies (SLF4J only)
- API depends on core, not vice versa

## Development Workflow

### Working on kabka-core (Library Module)

Since `kabka-core` is a library (not a runnable app), use test-driven development:

**Option 1: Auto-run tests on changes**
```bash
./gradlew :kabka-core:test --continuous
```
Automatically runs tests whenever you save a file in kabka-core.

**Option 2: Manual test runs**
```bash
# Edit kabka-core files, then run:
./gradlew :kabka-core:test

# Run a specific test class:
./gradlew :kabka-core:test --tests KabkaEngineTest

# Run tests with more details:
./gradlew :kabka-core:test --info
```

**Workflow:**
1. Edit code in `kabka-core/src/main/java/dev/kabka/core/...`
2. Edit/create test in `kabka-core/src/test/java/dev/kabka/core/...`
3. Run tests (auto or manual)
4. Iterate until tests pass

### Working on kabka-api (Hot Reload)

The project includes **Spring Boot DevTools** for automatic reload when you make changes.

**Two-Terminal Setup:**

**Terminal 1: Run the application**
```bash
./gradlew :kabka-api:bootRun
```
Starts the Spring Boot app on port 8080 and keeps it running.

**Terminal 2: Watch for changes (both kabka-api AND kabka-core)**
```bash
./gradlew :kabka-core:classes :kabka-api:classes --continuous
```
Automatically recompiles both modules when you save Java files.

**How it works:**
1. Edit any `.java` file (in kabka-api or kabka-core) and save
2. Terminal 2 detects the change → recompiles (1-2 seconds)
3. DevTools in Terminal 1 detects new `.class` files → restarts app automatically
4. Changes are live! No manual restart needed.

You'll see this in Terminal 1 when changes are detected:
```
INFO ... Restarting due to 1 class path change...
INFO ... Started KabkaApiApplication in 0.3 seconds
```

### When to Use Clean Build

| Command | Purpose | When to Use |
|---------|---------|-------------|
| `./gradlew :kabka-api:bootRun` | Start the app | Once per dev session |
| `./gradlew :kabka-core:classes :kabka-api:classes --continuous` | Auto-compile both modules | Once per dev session |
| `./gradlew :kabka-core:test --continuous` | Auto-run core tests | When working only on core |
| `./gradlew clean build` | Full rebuild from scratch | Rarely (switching branches, troubleshooting) |
| `./gradlew test` | Run all tests | Before committing |
| `./gradlew :kabka-core:test` | Test core module only | After core changes |

**Note:** Avoid `clean build` during active development - it's slow and unnecessary!

### Step-by-Step Development

**Scenario 1: Working on Core Logic Only**
```bash
# Terminal 1: Auto-run tests
./gradlew :kabka-core:test --continuous

# Edit: kabka-core/src/main/java/dev/kabka/core/...
# Edit: kabka-core/src/test/java/dev/kabka/core/...
# Tests run automatically on save
```

**Scenario 2: Working on API + Core Together**
```bash
# Terminal 1: Run the app
./gradlew :kabka-api:bootRun

# Terminal 2: Auto-compile both modules
./gradlew :kabka-core:classes :kabka-api:classes --continuous

# Edit files in either module
# App auto-reloads on save

# Terminal 3: Test with curl
curl http://localhost:8080/api/...
```

**Scenario 3: Working on API Only**
```bash
# Terminal 1: Run the app
./gradlew :kabka-api:bootRun

# Terminal 2: Auto-compile API only
./gradlew :kabka-api:classes --continuous

# Edit: kabka-api/src/main/java/dev/kabka/api/controller/...
# App auto-reloads on save
```
   # Terminal 3 (while app is running)
   curl http://localhost:8080/api/...
   ```

## Next Steps

1. **Implement Core Features**
   - [ ] Topic management (create, delete, list)
   - [ ] Message storage (in-memory → disk)
   - [ ] Consumer groups
   - [ ] Replication across brokers

2. **Enhance API**
   - [ ] Consumer endpoints (subscribe, poll)
   - [ ] Producer batching
   - [ ] Admin operations (metrics, config)

3. **Add Persistence**
   - [ ] File-based storage
   - [ ] WAL (Write-Ahead Log)

4. **Distributed Features**
   - [ ] Leader election
   - [ ] Partition rebalancing
   - [ ] Cluster coordination

## Key Files

| File | Purpose |
|------|---------|
| [settings.gradle.kts](settings.gradle.kts) | Module declarations |
| [build.gradle.kts](build.gradle.kts) | Shared build config |
| [kabka-core/build.gradle.kts](kabka-core/build.gradle.kts) | Core dependencies |
| [kabka-api/build.gradle.kts](kabka-api/build.gradle.kts) | API dependencies |
| [KabkaEngine.java](kabka-core/src/main/java/dev/kabka/core/KabkaEngine.java) | Core engine |
| [KabkaApiApplication.java](kabka-api/src/main/java/dev/kabka/api/KabkaApiApplication.java) | Spring Boot app |

---

**Built with:** Java 25, Spring Boot 4.0.1, Gradle 9.2.1
