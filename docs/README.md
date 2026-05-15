# Tracko - Project Overview

Tracko is a field engineer management platform that provides end-to-end operational management for field service businesses.

## Quick Links
- [API Reference (Swagger)](http://localhost:8080/swagger-ui.html)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Setup Guide](#quick-start)

## Architecture Summary

```
Mobile (Kotlin/Jetpack Compose)
        │
        ▼
Nginx Reverse Proxy (SSL, Rate Limiting, CSP/HSTS)
        │
        ▼
Spring Boot API (Java 17, REST + WebSocket)
        │
   ┌────┼────────┐
   ▼    ▼        ▼
PostgreSQL  Redis  MinIO
 (internal) (auth) (internal)
```

## Key Features
1. **Live Tracking** — Real-time GPS with geofencing
2. **Attendance** — Automated mark-in/out with exceptions
3. **Visits & Reports** — Scheduling, call reports with photos
4. **Quotations** — PDF generation with approval workflow
5. **Scorecards** — Performance scoring & rankings
6. **Admin Panel** — RBAC, audit logs, system configuration

## Local Development
```bash
# Database-first approach (no Docker)
docker compose up tracko-db tracko-redis tracko-minio
./mvnw spring-boot:run

# Full stack with Docker
docker compose up -d
```

## Repository Structure
| Directory | Purpose |
|-----------|---------|
| `backend/` | Spring Boot REST API |
| `web/manager-portal/` | Manager React SPA |
| `web/admin-portal/` | Admin React SPA |
| `android/` | Android client (Kotlin) |
| `database/` | Flyway migrations & scripts |
| `docker/` | Nginx & PostgreSQL configs |

## Contact
Tracko Development Team