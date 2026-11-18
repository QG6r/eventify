# Eventify — event ticket booking service

[![CI](https://github.com/QG6r/eventify/actions/workflows/ci.yml/badge.svg)](https://github.com/QG6r/eventify/actions/workflows/ci.yml)

A full-stack ticket booking application. Users browse events and book seats; administrators manage events and confirm bookings.
The backend is a Spring Boot REST API with PostgreSQL; the frontend is a React + TypeScript single-page app (`booking-ui`).

Final project of the *Java Web Application Development* course (Skillbox).

## Features

- Registration and login with **JWT** authentication, roles `USER` and `ADMIN`
- Event catalogue with pagination and date filtering
- Ticket booking with seat availability checks and **race-condition protection** (pessimistic locking, see below)
- Booking management: view, change and cancel your own bookings
- Admin panel: create, edit and delete events, list all bookings, confirm bookings
- Notification settings per user (new users get sensible defaults; Telegram linking is groundwork only, no messages are sent yet)
- Uniform JSON error format and interactive API docs (Swagger UI)
- Flyway database migrations and optional demo data

## Tech stack

| Area | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA (Hibernate), Spring Security |
| Database | PostgreSQL 16, Flyway migrations |
| Auth | JWT (jjwt), BCrypt password hashing |
| Mapping / docs | MapStruct, springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, AssertJ |
| Frontend | React 19, TypeScript, React Router, React Hook Form + Yup, Axios, Tailwind CSS |
| Build / run | Gradle, Docker, Docker Compose, GitHub Actions CI |

## Quick demo

### Option 1 — frontend only, no backend needed (fastest)

The frontend ships with a built-in mock API, so the UI can be shown without a database.

```bash
cd booking-ui
npm ci
npm run start:mocks      # http://localhost:3000
```

Mock accounts: `user@example.com` / `password123` and `admin@example.com` / `password123`.

### Option 2 — full stack with Docker (recommended)

Requires Docker and Docker Compose.

```bash
cp .env.example .env
docker compose up -d --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5433 |

The first start applies all Flyway migrations and loads **demo data** (six upcoming events and one demo booking).

| Account | Email | Password | Role |
|---|---|---|---|
| Administrator | `admin@eventify.local` | `admin12345` | ADMIN |
| Demo user | `demo@eventify.local` | `demo12345` | USER |

These credentials are for demonstration only. For a real deployment, change them and set a strong `JWT_SECRET`.

To start without demo data, add `FLYWAY_LOCATIONS=classpath:db/migration` to `.env`. To reset the database completely: `docker compose down -v`.

### Option 3 — backend from the IDE or Gradle

```bash
docker compose up -d db          # only the database
./gradlew bootRun                # connects to localhost:5433 using the defaults from application.yml
```

## How booking works

1. A user creates a booking for an event. The seats are held until `expiryTime` (default 30 minutes, configurable with `BOOKING_EXPIRY_MINUTES`).
2. An administrator confirms the booking; confirmed bookings keep their seats permanently.
3. Seats of unconfirmed bookings whose hold has expired are released automatically: availability is calculated from confirmed bookings plus bookings that have not expired yet.
4. When creating or changing a booking, the event row is read with a **pessimistic write lock** (`PESSIMISTIC_WRITE`). Two users cannot take the last seats at the same time: the second request waits for the first transaction and then sees the updated availability (and receives a `409 Conflict` if there are not enough seats).

## API overview

Full interactive documentation is available at `/swagger-ui.html`.

| Area | Endpoints | Access |
|---|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` | public |
| Events | `GET /events`, `GET /events/{id}` | public |
| Bookings | `GET /bookings`, `POST /bookings`, `GET/PUT/DELETE /bookings/{id}` | authenticated (own bookings) |
| User settings | `GET/PUT/DELETE /user/notifications`, `POST /user/telegram/link` | authenticated |
| Admin | `POST /admin/events`, `PUT/DELETE /admin/events/{id}`, `GET /admin/bookings`, `PUT /admin/bookings/{id}/confirm`, `DELETE /admin/bookings/{id}` | `ADMIN` |

## Project structure

```
src/main/java/eventify/
├── controller/    REST controllers
├── service/       business logic (interfaces) and service/impl (implementations)
├── repository/    Spring Data JPA repositories
├── entity/        JPA entities (User, Event, Booking, NotificationPreferences)
├── dto/           request/response objects
├── mapper/        MapStruct mappers
├── security/      JWT service and filter, user details
├── config/        Spring Security, OpenAPI, JWT properties
└── exception/     custom exceptions and the global error handler
src/main/resources/
├── application.yml
└── db/
    ├── migration/ Flyway migrations V1..V7 (schema, admin account)
    └── demo/      optional demo data (V100)
booking-ui/        React + TypeScript frontend
```

## Configuration

Environment variables (see `.env.example`):

| Variable | Purpose | Default |
|---|---|---|
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | database credentials (Docker Compose) | see `.env.example` |
| `CORS_ALLOWED_ORIGINS` | comma-separated browser origins allowed to call the API | `http://localhost:3000,http://localhost:3001` |
| `JWT_SECRET` | key used to sign tokens, at least 32 bytes | development value, **change it** |
| `JWT_EXPIRATION_MS` | token lifetime | 86400000 (24 h) |
| `BOOKING_EXPIRY_MINUTES` | how long unconfirmed bookings hold seats | 30 |
| `FLYWAY_LOCATIONS` | set to `classpath:db/migration` to skip demo data | migrations + demo |

## Tests and CI

```bash
./gradlew test                        # backend: service-layer unit tests and JWT tests
cd booking-ui && npm test -- --watchAll=false   # frontend tests
```

GitHub Actions (`.github/workflows/ci.yml`) builds and tests both the backend and the frontend on every push and pull request.

## Known limitations and next steps

- Notifications are stored but not delivered (Telegram integration is a stub).
- Integration tests with a real PostgreSQL (Testcontainers) would be the next step for the booking concurrency logic.
