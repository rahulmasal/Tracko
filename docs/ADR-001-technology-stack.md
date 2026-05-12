# Tracko - Architecture Decision Records

This directory contains ADRs (Architecture Decision Records) for the Tracko Field Engineer Management System.

## ADR-001: Technology Stack Selection
- **Status:** Accepted
- **Date:** 2026-05-12
- **Context:** Full-stack field engineer management platform requiring real-time tracking, offline-first mobile, and enterprise-grade web portals.
- **Decision:** Java 17 + Spring Boot 3.2 (backend), React 18 + TypeScript (web), Kotlin + Jetpack Compose (Android), PostgreSQL 15, Redis 7, MinIO S3-compatible storage.
- **Rationale:** Spring Boot provides robust enterprise features (Security, JPA, WebSocket). React ecosystem offers rich UI libraries. Kotlin is Google's preferred Android language. PostgreSQL offers advanced JSON and GIS support.

## ADR-002: Micro-Services vs Monolith
- **Status:** Accepted
- **Date:** 2026-05-12
- **Context:** Project needs to balance development simplicity with scalability.
- **Decision:** Modular monolith — single codebase with clear module boundaries (Backend, Admin Portal, Manager Portal, Mobile).
- **Rationale:** Lower operational complexity for a startup-size team. Docker Compose enables easy local development. Modules can be extracted to micro-services later if needed.

## ADR-003: Real-Time Architecture
- **Status:** Accepted
- **Date:** 2026-05-12
- **Context:** Field engineers need live location updates and managers need real-time visibility.
- **Decision:** WebSocket for bidirectional real-time communication, Redis Pub/Sub for internal event broadcasting.
- **Rationale:** WebSocket provides low-latency push from server to clients. Redis Pub/Sub decouples internal components.

## ADR-004: Authentication Strategy
- **Status:** Accepted
- **Date:** 2026-05-12
- **Context:** Multi-role access (Admins, Managers, Field Engineers) with mobile and web clients.
- **Decision:** JWT (stateless) with role-based access control (RBAC), refresh token rotation.
- **Rationale:** Stateless auth scales horizontally. RBAC maps to Spring Security's method-level security. Refresh tokens enable long-lived sessions without re-authentication.