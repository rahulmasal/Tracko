# Tracko - Field Engineer Management System

A comprehensive field engineer management platform with real-time tracking, attendance management, visit scheduling, call reports, enquiry/lead management, quotation generation, leave management, and performance scorecards.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Mobile App (Android)                       │
│              (Field Engineer - Kotlin + Jetpack Compose)             │
└─────────────────────────────┬───────────────────────────────────────┘
                              │ HTTPS / WebSocket
                              ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         Nginx Reverse Proxy                          │
│                    (Load Balancer + SSL Termination)                  │
└──┬──────────────────────┬──────────────────────┬────────────────────┘
   │ /manager/*            │ /admin/*              │ /api/* , /ws/*
   ▼                       ▼                       ▼
┌──────────────┐   ┌──────────────┐   ┌───────────────────────────┐
│   Manager    │   │    Admin     │   │   Spring Boot Backend     │
│   Portal     │   │   Portal     │   │   (REST API + WebSocket)  │
│  (React/TS)  │   │  (React/TS)  │   │   Java 17 + Spring Boot  │
│   :3000      │   │   :3001      │   │   :8080                   │
└──────────────┘   └──────────────┘   └────┬──────────────────────┘
                                           │
              ┌────────────────────────────┼────────────────────────┐
              │                            │                        │
              ▼                            ▼                        ▼
       ┌──────────┐                 ┌──────────┐             ┌──────────┐
       │PostgreSQL│                 │  Redis   │             │  MinIO   │
       │   15     │                 │    7     │             │  Object  │
       │ :5432    │                 │ :6379    │             │ Storage  │
       └──────────┘                 └──────────┘             │ :9000    │
                                                            └──────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3, Spring Security, JPA/Hibernate |
| Web Portals | React 18, TypeScript, Vite, MUI 5, AG Grid, Recharts |
| Mobile | Kotlin, Jetpack Compose, Google Maps SDK |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Object Storage | MinIO (S3-compatible) |
| Reverse Proxy | Nginx |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## Prerequisites

- Java 17+
- Node.js 20+
- Docker & Docker Compose
- Android Studio (for mobile development)
- PostgreSQL 15 (local development without Docker)

## Quick Start (Docker)

```bash
# Clone the repository
git clone https://github.com/your-org/tracko.git
cd tracko

# Copy environment configuration
cp .env.example .env

# Start all services
docker-compose up -d

# Wait for services to initialize, then access:
# Manager Portal:  http://localhost:3000
# Admin Portal:    http://localhost:3001
# API:             http://localhost:8080/api
```

## Development Setup

### Backend

```bash
cd backend

# Build and run
./mvnw spring-boot:run

# Or build JAR and run
./mvnw clean package -DskipTests
java -jar target/tracko-backend-*.jar
```

### Web Portals

```bash
# Manager Portal
cd web/manager-portal
npm install
npm run dev          # http://localhost:3000

# Admin Portal
cd web/admin-portal
npm install
npm run dev          # http://localhost:3001
```

### Mobile App

Open `android/` in Android Studio and run on device or emulator.

## Project Structure

```
tracko/
├── backend/                    # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tracko/
│   │   │   │   ├── config/         # Security, WebSocket, CORS config
│   │   │   │   ├── controller/     # REST controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── entity/         # JPA entities
│   │   │   │   ├── repository/     # Spring Data repositories
│   │   │   │   ├── service/        # Business logic
│   │   │   │   └── TrackoApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
│
├── web/
│   ├── manager-portal/         # Manager React SPA
│   │   ├── src/
│   │   │   ├── components/     # Dashboard, Attendance, Map, etc.
│   │   │   ├── pages/         # Route pages
│   │   │   ├── services/      # API service layer
│   │   │   ├── hooks/         # Zustand stores
│   │   │   ├── utils/         # Helpers, constants
│   │   │   └── styles/        # Theme, global CSS
│   │   ├── Dockerfile
│   │   └── package.json
│   │
│   ├── admin-portal/           # Admin React SPA
│   │   ├── src/
│   │   │   ├── components/     # Dashboard, Users, Config, etc.
│   │   │   ├── pages/         # Route pages
│   │   │   ├── services/      # API service layer
│   │   │   └── styles/        # Theme, global CSS
│   │   ├── Dockerfile
│   │   └── package.json
│   │
│   └── shared/                 # Shared web utilities
│       ├── components/
│       │   └── ProtectedRoute.tsx
│       └── utils/
│           ├── auth.ts
│           ├── formatting.ts
│           └── validation.ts
│
├── database/
│   ├── migrations/
│   │   ├── V1__initial_schema.sql
│   │   └── V2__seed_data.sql
│   └── scripts/
│       ├── init.sql
│       └── backup.sh
│
├── docker/
│   ├── nginx/
│   │   └── nginx.conf
│   └── postgres/
│       └── init.sql
│
├── scripts/
│   ├── setup-dev.bat
│   └── setup-dev.sh
│
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       └── web-ci.yml
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | localhost |
| `DB_PORT` | PostgreSQL port | 5432 |
| `DB_NAME` | Database name | tracko_db |
| `DB_USER` | Database user | tracko_user |
| `DB_PASSWORD` | Database password | tracko_pass_2024 |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `MINIO_ENDPOINT` | MinIO server URL | http://localhost:9000 |
| `MINIO_ACCESS_KEY` | MinIO access key | tracko_admin |
| `MINIO_SECRET_KEY` | MinIO secret key | tracko_minio_2024 |
| `JWT_SECRET` | JWT signing secret | (change in production) |
| `FCM_SERVER_KEY` | Firebase Cloud Messaging key | (for push notifications) |
| `TWILIO_ACCOUNT_SID` | Twilio account SID | (for SMS) |
| `SMTP_HOST` | SMTP server | smtp.gmail.com |
| `SMTP_PORT` | SMTP port | 587 |

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@tracko.com | Admin@123 |

## API Documentation

API documentation is available via Swagger UI when the backend is running:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/api-docs

## License

Proprietary - All Rights Reserved
