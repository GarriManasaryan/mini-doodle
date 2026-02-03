# Mini Doodle

This repository contains a backend implementation of a simplified meeting scheduling platform (Mini Doodle).
The focus is on availability management, meeting scheduling, and a clear separation of concerns between
availability and bookings.

The core architectural decisions are documented in a dedicated **[RFC: Calendar-based TimeSlot & Meeting Scheduling Model](.docs/rfc-002-calendar-timeslot-meeting-model.md)**


This README explains how to run the project locally and how the system is structured at a high level.

---

## High-level Overview

The scheduling model is built around three core concepts:

- Calendar  
  Defines ownership, responsibility, and working hours. Acts as a context boundary.

- TimeSlot  
  Represents declared availability. Can be single or recurring. Is never mutated by meetings.

- ScheduledItem  
  Represents actual usage of time (meetings, focus time, etc.). Can overlap with other items.

Key design principles:

- Availability (TimeSlot) and bookings (ScheduledItem) are intentionally decoupled
- Meetings never modify or split availability
- Recurring events are evaluated dynamically, not pre-expanded
- Database schema is owned by Flyway migrations
- Soft deletes are used for auditability and safety

For full rationale and alternatives considered, see the RFC.

---

## Tech Stack

- Java 21
- Spring Boot
- PostgreSQL 16
- Flyway (database migrations)
- Docker and Docker Compose

---

## Local Setup and Installation

### Prerequisites

- Docker with Docker Compose
- JDK 21 (only required to build the JAR)
- Gradle or Gradle Wrapper

---

## Step 1: Environment configuration

Create a `.env` file in both locations:

- `.docker/.env`
- `backend/.env` (or configure environment variables in your IDE)

Example:

PG_USERNAME=postgres  
PG_PASSWORD=1  
PG_DATABASE_NAME=doodle_mini_db
PG_HOST=localhost

* when launching the backend from intelj (PG_HOST=localhost), 
* from docker (PG_HOST=doodle_pg, container name from docker-compose)
---

## Step 2: Build the application JAR

The backend container expects a pre-built JAR.

Open *build.gradle* and run **copyAppJar** 

This produces:

backend/build/output-docker/app.jar

---

## Step 3: Start the database (PostgreSQL only)

Start only the database service:

```bash

docker compose up database --build
```

PostgreSQL will be available on port 5433.

---

## Step 4: Create the database (one-time step)

Connect to PostgreSQL inside the container:

* -U → pg_username from .env

```bash

docker exec -it doodle_pg psql -U postgres -h localhost -p 5433
```

Create the database:
```bash

create database doodle_mini_db;
```

---

## Step 5: Start the full application (runs migrations)

Start all services:

```bash

docker compose up --build
```

What happens:

- PostgreSQL is already running
- Backend starts
- Flyway automatically applies all database migrations
- Database schema is ready for use

Alternatively you can spin up only the database and launch the app from Intelj 

---

## Step 6: (Optional) Insert sample data

Sample data is provided in .docker/seed/seed.sql

Apply manually via console or terminal:

```bash

docker exec -i doodle_pg psql -U postgres -d doodle_mini_db < .docker/seed/seed.sql
```

This inserts users, calendars, working hours, time slots (single and recurring),
meetings, and focus time entries.

Seeding is explicit by design to avoid race conditions with migrations.

---

## Test APIs after local launch

http://localhost:8080/swagger-ui/index.html#/