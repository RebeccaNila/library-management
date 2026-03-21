# 12-Factor App Conformance

This document maps the Library Management System against the
12-Factor App methodology (https://12factor.net).

---

## What is 12-Factor App?

A methodology for building modern, scalable, maintainable software-as-a-service apps.
Each factor addresses a specific aspect of application design and deployment.


---

## Conformance Summary

| Factor | Name | Status | Notes |
|--------|------|--------|-------|
| I | Codebase | ✅ | Single repo on GitHub |
| II | Dependencies | ✅ | Maven manages all dependencies |
| III | Config | ✅ | Environment variables via profiles |
| IV | Backing Services | ✅ | MySQL treated as attached resource |
| V | Build, Release, Run | ✅ | Docker multi-stage build |
| VI | Processes | ✅ | Stateless Spring Boot app |
| VII | Port Binding | ✅ | Configurable via SERVER_PORT |
| VIII | Concurrency | ⚠️ | Single instance — scalable via K8s |
| IX | Disposability | ✅ | Fast startup, graceful shutdown |
| X | Dev/Prod Parity | ✅ | Dev and prod profiles |
| XI | Logs | ⚠️ | Basic logging — no log aggregation |
| XII | Admin Processes | ⚠️ | Not explicitly implemented |

---

## Factor Details

### I. Codebase — One codebase, many deploys ✅
```
Single GitHub repository
        ↓
Same code deployed to:
→ Local (dev profile)
→ Docker (prod profile)
→ Kubernetes (prod profile)
```

### II. Dependencies — Explicitly declare dependencies ✅
```
All dependencies declared in pom.xml
→ No reliance on system-wide packages
→ Maven downloads exact versions
→ Reproducible builds guaranteed
```

### III. Config — Store config in environment ✅
```
No hardcoded config in codebase
        ↓
Dev  → application-dev.properties
Prod → environment variables via .env / K8s ConfigMap + Secret

Examples:
→ DB_URL → injected at runtime
→ MYSQL_PASSWORD → injected from K8s Secret
→ SPRING_PROFILES_ACTIVE → injected from ConfigMap
```

### IV. Backing Services — Treat backing services as attached resources ✅
```
MySQL is treated as an attached resource
→ Connected via URL in environment variable
→ Can swap local MySQL for cloud MySQL
   without changing any code
→ Just change DB_URL env var ✅
```

### V. Build, Release, Run — Strictly separate stages ✅
```
Build stage   → Docker multi-stage build compiles JAR
Release stage → JAR + config (env vars) combined
Run stage     → Container runs the JAR

docker build → creates immutable image
docker run   → runs image with env vars injected
```

### VI. Processes — Execute as stateless processes ✅
```
Spring Boot app is completely stateless
→ No session data stored in app
→ No local file storage
→ All state stored in MySQL database
→ Any pod can handle any request ✅
```

### VII. Port Binding — Export services via port binding ✅
```
App binds to port defined by SERVER_PORT env var
→ Default: 8080
→ Configurable without code change
→ Docker: maps to host port via docker-compose
→ K8s: exposed via NodePort service on 30080
```

### VIII. Concurrency — Scale out via the process model ⚠️
```
Currently runs as single instance
→ K8s deployment supports scaling:
   kubectl scale deployment/library-api-deployment
   --replicas=3 -n library-management
→ Stateless design supports horizontal scaling ✅
→ Not implemented by default but ready for it
```

### IX. Disposability — Fast startup and graceful shutdown ✅
```
Spring Boot starts in seconds
→ K8s can restart pods quickly
→ docker-compose restart policy: on-failure
→ K8s restart policy: on-failure
→ No cleanup needed on shutdown (stateless)
```

### X. Dev/Prod Parity — Keep dev, staging, prod as similar as possible ✅
```
Same technology stack in all environments:
→ Same MySQL 8.0 version
→ Same Java 17
→ Same Spring Boot version
→ Same Docker image in K8s as local Docker

Differences minimized:
→ Dev: local MySQL, show-sql=true
→ Prod: containerized MySQL, show-sql=false
```

### XI. Logs — Treat logs as event streams ⚠️
```
Currently:
→ Spring Boot logs to stdout ✅ (12-factor compliant)
→ kubectl logs captures stdout ✅
→ No log aggregation service (ELK, Datadog) ❌

For production improvement:
→ Add centralized logging (ELK Stack or similar)
```

### XII. Admin Processes — Run admin tasks as one-off processes ⚠️
```
Currently:
→ Database migrations handled by Hibernate ddl-auto
→ No explicit admin process scripts

For production improvement:
→ Add Flyway or Liquibase for DB migrations
→ Run as one-off K8s Job before deployment
```

---

## Summary

The Library Management System conforms to 9 out of 12 factors fully,
with 3 factors partially implemented. The partial implementations are
acceptable for the scope of this project and are documented as
improvement areas for production readiness.

### Fully Implemented (9/12)
```
✅ I   — Single codebase on GitHub
✅ II  — Maven dependency management
✅ III — Environment-based config
✅ IV  — MySQL as attached backing service
✅ V   — Docker multi-stage build/release/run
✅ VI  — Stateless Spring Boot processes
✅ VII — Configurable port binding
✅ IX  — Fast startup disposable containers
✅ X   — Dev/prod parity via profiles
```

### Partially Implemented (3/12)
```
⚠️ VIII — Concurrency: stateless but single instance
⚠️ XI   — Logs: stdout only, no aggregation
⚠️ XII  — Admin: no formal migration process
```
