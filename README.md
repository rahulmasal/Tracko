<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot 3.2"/>
  <img src="https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=white" alt="React 18"/>
  <img src="https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript 5"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL 15"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
</p>

<h1 align="center">Tracko</h1>
<p align="center">
  <strong>Field Engineer Management System</strong><br>
  Real-time tracking · Attendance · Visits · Quotations · Scorecards
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-project-structure">Structure</a>
</p>

---

## Overview

Tracko is a comprehensive platform for managing field engineer operations. It provides real-time GPS tracking, attendance management, visit scheduling, enquiry/lead management, quotation generation, leave management, and performance scorecards — all through dedicated web portals for managers and admins.

## Features

| Module | Capabilities |
|--------|-------------|
| **Live Tracking** | Real-time GPS tracking with Leaflet maps, geofencing, ping-based location updates |
| **Attendance** | Mark-in/Mark-out with exceptions, team attendance overview, auto-reminders |
| **Visits & Calls** | Visit scheduling, call report submission with photo attachments, pending reports review |
| **Enquiries & Leads** | Lead capture, enquiry pipeline management, status tracking |
| **Quotations** | PDF generation, approval workflow, timeline tracking, branded templates |
| **Leave Management** | Leave applications, team calendar view, approval/rejection workflow |
| **Scorecards** | Performance scoring, team rankings, configurable score formulas |
| **Admin Panel** | User & role management, branch & shift management, security policies, audit logs, system configuration |

## Tech Stack

```mermaid
mindmap
  root((Tracko))
    Frontend
      React 18
      TypeScript 5
      Vite
      MUI 5
      Zustand
      React Query
      AG Grid
      Recharts
      Leaflet
      react-hook-form
      Zod
    Backend
      Java 17
      Spring Boot 3.2
      Spring Security
      JPA / Hibernate
      Flyway
      MapStruct
      JWT
      Swagger / OpenAPI
      Quartz Scheduler
      iText PDF
      Apache POI
      Twilio SMS
      Firebase Push
    Mobile
      Kotlin
      Jetpack Compose
      Google Maps SDK
    Data &amp; Infra
      PostgreSQL 15
      Redis 7
      MinIO S3
      Nginx
      Docker
      GitHub Actions
```

## Architecture

```mermaid
flowchart TB
    subgraph Clients["Clients"]
        Mobile["📱 Mobile App<br/>Kotlin · Jetpack Compose"]
        Manager["👔 Manager Portal<br/>React 18 · TypeScript<br/>:3000"]
        Admin["⚙️ Admin Portal<br/>React 18 · TypeScript<br/>:3001"]
    end

    subgraph Proxy["Reverse Proxy"]
        Nginx["🌐 Nginx<br/>SSL · Load Balancer"]
    end

    subgraph Backend["Backend"]
        API["☕ Spring Boot API<br/>Java 17 · REST · WebSocket<br/>:8080"]
    end

    subgraph Storage["Data Layer"]
        DB[("🗄️ PostgreSQL 15<br/>:5432")]
        Cache[("⚡ Redis 7<br/>:6379")]
        ObjectStore[("📦 MinIO S3<br/>:9000")]
    end

    Mobile -->|HTTPS / WebSocket| Nginx
    Manager -->|HTTPS| Nginx
    Admin -->|HTTPS| Nginx

    Nginx -->|/api/* /ws/*| API
    Nginx -->|/manager/*| Manager
    Nginx -->|/admin/*| Admin

    API --> DB
    API --> Cache
    API --> ObjectStore

    style Clients fill:#e1f5fe,stroke:#0288d1
    style Proxy fill:#fff3e0,stroke:#f57c00
    style Backend fill:#e8f5e9,stroke:#388e3c
    style Storage fill:#fce4ec,stroke:#d32f2f
```

## Data Flow

```mermaid
sequenceDiagram
    participant E as Field Engineer
    participant M as Manager Portal
    participant A as Admin Portal
    participant API as Backend API
    participant DB as PostgreSQL
    participant R as Redis
    participant S as MinIO

    Note over E,S: Attendance Flow
    E->>API: Mark-in with GPS location
    API->>DB: Save attendance record
    API->>R: Cache active status
    API-->>M: Push live update (WebSocket)

    Note over E,S: Visit Flow
    M->>API: Assign visit to engineer
    API->>DB: Create visit record
    API-->>E: Push notification (FCM)
    E->>API: Submit call report + photos
    API->>S: Store photo attachments
    API->>DB: Update visit status
    API-->>M: Notify report ready for review

    Note over E,S: Quotation Flow
    E->>API: Generate quotation
    API->>S: Create PDF
    API->>DB: Save quotation data
    API-->>M: Push approval request
    M->>API: Approve / reject
    API-->>E: Notify decision
```

## Quick Start

### Prerequisites

- Java 17+
- Node.js 20+
- Docker & Docker Compose
- PostgreSQL 15 (for local dev without Docker)

### Docker (Recommended)

```bash
git clone https://github.com/your-org/tracko.git
cd tracko
cp .env.example .env
docker compose up -d
```

| Service | URL |
|---------|-----|
| Manager Portal | http://localhost:3000 |
| Admin Portal | http://localhost:3001 |
| API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### Local Development

**Backend**

```bash
cd backend
./mvnw spring-boot:run
```

**Web Portals**

```bash
cd web/manager-portal
npm install && npm run dev

cd web/admin-portal   # separate terminal
npm install && npm run dev
```

**Mobile App**

Open `android/` in Android Studio and run on device or emulator.

## Project Structure

```mermaid
graph LR
    subgraph tracko["tracko/"]
        direction TB
        BE["backend/<br/>Spring Boot API"]
        WEB["web/"]
        ANDROID["android/<br/>Kotlin App"]
        DB["database/<br/>Migrations"]
        DOCKER["docker/<br/>Nginx · Init"]
        SCRIPTS["scripts/"]
        CI[".github/workflows/<br/>CI/CD"]
        DC["docker-compose.yml"]
    end

    subgraph backend_detail["backend/"]
        SRC["src/main/java/com/tracko/"]
        POM["pom.xml"]
    end

    subgraph web_detail["web/"]
        MP["manager-portal/<br/>React 18 · :3000"]
        AP["admin-portal/<br/>React 18 · :3001"]
        SHARED["shared/<br/>Auth · Utils"]
    end

    subgraph java_pkg["src/main/java/com/tracko/"]
        CFG["config/"]
        CTRL["controller/"]
        DTO["dto/"]
        ENT["entity/"]
        REPO["repository/"]
        SVC["service/"]
        MAIN["TrackoApplication.java"]
    end

    tracko --> BE
    tracko --> WEB
    tracko --> ANDROID
    tracko --> DB
    tracko --> DOCKER
    tracko --> SCRIPTS
    tracko --> CI
    tracko --> DC

    BE --> backend_detail
    backend_detail --> SRC
    backend_detail --> POM

    WEB --> web_detail
    web_detail --> MP
    web_detail --> AP
    web_detail --> SHARED

    SRC --> java_pkg
```

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@tracko.com | Admin@123 |

## API Documentation

Available via Swagger UI when the backend is running:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## License

Proprietary — All Rights Reserved
