# MediCare Hospital Management System

An enterprise-grade Hospital Management System built with Java Spring Boot, React, PostgreSQL, Kafka, and Redis.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3, Spring Security |
| Frontend | React 18, TypeScript, Tailwind CSS |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Messaging | Apache Kafka |
| Auth | JWT (Role-based: Admin, Doctor, Patient) |
| DevOps | Docker, Docker Compose, GitHub Actions CI/CD |
| Testing | JUnit 5, Mockito |
| Docs | Swagger / OpenAPI |

## Features

- Patient registration and profile management
- Doctor scheduling and appointment booking
- Real-time event notifications via Kafka (confirmed, cancelled, completed)
- Role-based authentication — Admin, Doctor, Patient using JWT
- REST APIs for all operations (Swagger UI at /swagger-ui.html)
- Redis caching for frequently accessed data (doctors, appointments)
- React dashboard per role with appointment management
- Docker containerization for all services
- GitHub Actions CI/CD pipeline

## Architecture

```
┌─────────────────┐     ┌──────────────────────────────────────┐
│  React Frontend │────▶│           Spring Boot API             │
│  (Port 3000)    │     │           (Port 8080)                 │
└─────────────────┘     └────┬──────────┬──────────┬───────────┘
                             │          │          │
                     ┌───────▼──┐ ┌─────▼──┐ ┌───▼──────┐
                     │PostgreSQL│ │ Redis  │ │  Kafka   │
                     │(Port5432)│ │(Port   │ │(Port9092)│
                     └──────────┘ │ 6379)  │ └──────────┘
                                  └────────┘
```

## Quick Start

### Prerequisites
- Docker Desktop (running)
- Java 17
- Maven
- Node.js 20+

### 1. Clone and run with Docker Compose

```bash
git clone https://github.com/VaishnaviP600/hospital-management-system.git
cd hospital-management-system
docker-compose up --build
```

### 2. Run locally (development)

Start infrastructure only:
```bash
docker-compose up postgres redis kafka zookeeper
```

Run backend:
```bash
cd backend
mvn spring-boot:run
```

Run frontend:
```bash
cd frontend
npm install
npm start
```

## API Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | Public | Register user |
| POST | /api/auth/login | Public | Login |
| POST | /api/appointments | PATIENT | Book appointment |
| GET | /api/appointments/my | PATIENT | My appointments |
| GET | /api/appointments/doctor/my | DOCTOR | Doctor's appointments |
| PATCH | /api/appointments/{id}/status | DOCTOR/ADMIN | Update status |
| GET | /api/appointments | ADMIN | All appointments |
| GET | /api/doctors | Authenticated | List all doctors |
| GET | /api/patients/profile | PATIENT | Get profile |
| POST | /api/patients/profile | PATIENT | Update profile |

Full Swagger docs: http://localhost:8080/swagger-ui.html

## Running Tests

```bash
cd backend
mvn test
```

## Access

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

## GitHub Actions CI/CD

On every push to `main`:
1. Runs JUnit tests against a test PostgreSQL instance
2. Builds the React frontend
3. Builds Docker images for both services
