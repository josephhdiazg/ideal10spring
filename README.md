<div align="center">

# IDEAL10 - Property Tax Management System

Spring Boot API and Vue frontend for managing municipal property-tax data: properties, owners, liquidations, payments, fiscal configuration, clearance certificates, and dashboard metrics.

**Backend:** Spring Boot 4.0.6, Java 21, PostgreSQL/H2, Spring Security, JWT  
**Frontend:** Vue 3, Vite 7, Tailwind CSS, Flowbite  
**Build/ops:** Maven Wrapper, Docker, Docker Compose, GitHub Actions

[![CI - Tests](https://github.com/josephhdiazg/ideal10spring/actions/workflows/test.yml/badge.svg)](https://github.com/josephhdiazg/ideal10spring/actions/workflows/test.yml)
[![CI - Docker](https://github.com/josephhdiazg/ideal10spring/actions/workflows/deploy.yml/badge.svg)](https://github.com/josephhdiazg/ideal10spring/actions/workflows/deploy.yml)

</div>

---

## Contents

1. [Team](#team)
2. [Repository](#repository)
3. [Tech Stack](#tech-stack)
4. [Prerequisites](#prerequisites)
5. [Run With Docker Compose](#run-with-docker-compose)
6. [Run Locally](#run-locally)
7. [Configuration](#configuration)
8. [Seeded Development Users](#seeded-development-users)
9. [API Surface](#api-surface)
10. [Swagger UI](#swagger-ui)
11. [CI/CD](#cicd)

---

## Team

| Name | Role |
|---|---|
| Juan Gonzalez | Developer |
| Michael Arias | Developer |
| Joseph Diaz | Developer |
| Omar Gutierrez | Developer |

---

## Repository

Clone the public repository from GitHub:

```bash
git clone https://github.com/josephhdiazg/ideal10spring.git
cd ideal10spring
```

The repository includes GitHub Actions workflows for tests and Docker image publication. Pull requests and issues should target the GitHub repository.

---

## Tech Stack

| Layer | Technology | Version/source |
|---|---|---|
| Backend framework | Spring Boot | 4.0.6 (`pom.xml`) |
| Language | Java | 21 |
| Persistence | Spring Data JPA + JDBC | Spring Boot managed |
| Databases | PostgreSQL, H2 | PostgreSQL runtime, H2 runtime/test support |
| Security | Spring Security + JJWT | JJWT 0.12.7 |
| DTO mapping | MapStruct | 1.6.3 |
| Env loading | Spring Dotenv | 5.1.0 |
| API docs | Springdoc OpenAPI | 2.8.5 |
| Frontend | Vue, Vue Router | Vue 3.5.x |
| Frontend build | Vite | 7.x |
| Styling | Tailwind CSS, Flowbite, Flowbite Vue | Tailwind 3.4.x, Flowbite 4.x |
| Icons | Lucide Vue | 1.x |
| Containers | Docker, Docker Compose | Backend JRE image, frontend Nginx image |

---

## Prerequisites

For Docker Compose:

- Docker
- Docker Compose

For local development:

- Java 21
- PostgreSQL 16 or another PostgreSQL version compatible with the JDBC driver
- Node.js `^20.19.0` or `>=22.12.0` for Vite 7
- Maven is optional because the repository includes `./mvnw`

---

## Run With Docker Compose

```bash
git clone https://github.com/josephhdiazg/ideal10spring.git
cd ideal10spring

cp .env.example .env
# Edit .env and provide a real JWT_SECRET.

docker compose up --build
```

Default services:

| Service | URL/port |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

Stop services:

```bash
docker compose down
```

Reset the database volume:

```bash
docker compose down -v
```

---

## Run Locally

```bash
git clone https://github.com/josephhdiazg/ideal10spring.git
cd ideal10spring
```

### Backend

Create a PostgreSQL database and configure environment variables:

```bash
createdb ideal10spring
cp .env.example .env
```

Update `.env` with local database credentials and a real `JWT_SECRET`, then run:

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`.

During local development, `frontend/vite.config.js` proxies `/api` requests to `http://localhost:8080`.

---

## Configuration

`.env.example` defines the required backend settings:

| Variable | Required | Description | Example |
|---|---:|---|---|
| `DB_URL` | Yes | JDBC connection URL | `jdbc:postgresql://localhost:5432/ideal10spring` |
| `DB_USERNAME` | Yes | Database username | `ideal10spring` |
| `DB_PASSWORD` | Yes | Database password | `ideal10spring` |
| `DB_DRIVER` | Yes | JDBC driver class | `org.postgresql.Driver` |
| `JWT_SECRET` | Yes | Base64-encoded HMAC secret | generated value |
| `JWT_EXPIRATION` | Yes | Token lifetime in milliseconds | `86400000` |
| `SPRING_JPA_SHOW_SQL` | No | Overrides SQL logging when supplied | `false` |
| `VITE_API_BASE_URL` | No | Frontend build-time API base URL | `http://localhost:8080` |

Generate a development JWT secret:

```bash
openssl rand -base64 32
```

Do not commit `.env`; it is ignored by `.gitignore`.

---

## Seeded Development Users

`DataInitializer` seeds one user for each role if the user does not already exist:

| Email/username | Password | Role |
|---|---|---|
| `admin@ideal10.com` | `admin123` | `ADMINISTRADOR` |
| `hacienda@ideal10.com` | `hacienda123` | `FUNCIONARIO_HACIENDA` |
| `tesoreria@ideal10.com` | `tesoreria123` | `TESORERIA` |
| `contribuyente@ideal10.com` | `contribuyente123` | `CONTRIBUYENTE` |

These credentials are suitable for local development only.

---

## API Surface

Base path: `/api/v1`

Protected endpoints require:

```http
Authorization: Bearer <token>
```

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Authenticate and receive a JWT |
| `GET` | `/api/v1/auth/me` | Authenticated | Return the current username and roles |

Login request:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@ideal10.com",
  "password": "admin123"
}
```

Login response shape:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin@ideal10.com",
  "roles": ["ROLE_ADMINISTRADOR"]
}
```

### Properties

| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/v1/properties` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `GET` | `/api/v1/properties/{id}` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `POST` | `/api/v1/properties` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA` |
| `PUT` | `/api/v1/properties/{id}` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA` |
| `DELETE` | `/api/v1/properties/{id}` | Any authenticated user |
| `GET` | `/api/v1/properties/{propertyId}/owners` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `POST` | `/api/v1/properties/{propertyId}/owners` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA` |
| `PUT` | `/api/v1/properties/{propertyId}/owners/{ownerId}` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA` |
| `DELETE` | `/api/v1/properties/{propertyId}/owners/{ownerId}` | Any authenticated user |

### Liquidations, Payments, and Certificates

| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/v1/liquidations` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `GET` | `/api/v1/liquidations?propertyId={id}` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `GET` | `/api/v1/liquidations/{id}` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `POST` | `/api/v1/liquidations` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `CONTRIBUYENTE` |
| `GET` | `/api/v1/liquidations/{id}/payments` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA`, `CONTRIBUYENTE` |
| `POST` | `/api/v1/liquidations/{id}/payments` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `CONTRIBUYENTE` |
| `POST` | `/api/v1/liquidations/{id}/clearance-certificates` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `CONTRIBUYENTE` |
| `GET` | `/api/v1/clearance-certificates` | `ADMINISTRADOR`, `TESORERIA` |
| `GET` | `/api/v1/clearance-certificates/{id}` | `ADMINISTRADOR`, `TESORERIA` |

### Fiscal Configuration

These modules are restricted to `ADMINISTRADOR` and `TESORERIA`.

| Resource | Endpoints |
|---|---|
| Fiscal years | `GET /api/v1/fiscal-years`, `GET /api/v1/fiscal-years/active`, `GET /api/v1/fiscal-years/{id}`, `POST /api/v1/fiscal-years`, `PUT /api/v1/fiscal-years/{id}`, `DELETE /api/v1/fiscal-years/{id}` |
| Tax rates | `GET /api/v1/tax-rates`, `GET /api/v1/tax-rates/{id}`, `GET /api/v1/tax-rates/fiscal-year/{fiscalYearId}`, `POST /api/v1/tax-rates`, `PUT /api/v1/tax-rates/{id}`, `DELETE /api/v1/tax-rates/{id}` |
| Tax benefits | `GET /api/v1/tax-benefits`, `GET /api/v1/tax-benefits/{id}`, `GET /api/v1/tax-benefits/by-classification?classification={classification}`, `POST /api/v1/tax-benefits`, `PUT /api/v1/tax-benefits/{id}`, `DELETE /api/v1/tax-benefits/{id}` |
| Charge types | `GET /api/v1/charge-types`, `GET /api/v1/charge-types/{id}`, `POST /api/v1/charge-types`, `PUT /api/v1/charge-types/{id}`, `DELETE /api/v1/charge-types/{id}` |

### Other Modules

| Resource | Endpoints | Access |
|---|---|---|
| Owners | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` under `/api/v1/owners` | Any authenticated user |
| Municipalities | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` under `/api/v1/municipalities` | Any authenticated user |
| Dashboard | `GET /api/v1/dashboard/predial` | `ADMINISTRADOR`, `FUNCIONARIO_HACIENDA`, `TESORERIA` |

---

## Swagger UI

With the backend running:

```text
http://localhost:8080/swagger-ui.html
```

The raw OpenAPI document is served at:

```text
http://localhost:8080/api-docs
```

---

## CI/CD

Two workflows are defined under `.github/workflows`.

| Workflow | File | Trigger | Behavior |
|---|---|---|---|
| Test | `test.yml` | Push/PR to `main` or `develop`, manual dispatch | Runs `./mvnw test` on Java 21 |
| Deploy | `deploy.yml` | Push to `main` or `develop`, manual dispatch | Builds and pushes backend and frontend images to GHCR |

Published image names:

```text
ghcr.io/josephhdiazg/ideal10spring/backend:<branch>
ghcr.io/josephhdiazg/ideal10spring/backend:sha-<short-sha>
ghcr.io/josephhdiazg/ideal10spring/frontend:<branch>
ghcr.io/josephhdiazg/ideal10spring/frontend:sha-<short-sha>
```
