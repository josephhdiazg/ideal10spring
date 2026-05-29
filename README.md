<div align="center">

# IDEAL10 — Property Tax Management System

**Backend:** Spring Boot 4 · Java 21 · PostgreSQL &nbsp;|&nbsp; **Frontend:** Vue 3 · Vite · Tailwind CSS · Flowbite &nbsp;|&nbsp; **Auth:** JWT · Spring Security

[![CI – Tests](https://github.com/josephhdiazg/ideal10spring/actions/workflows/test.yml/badge.svg)](https://github.com/josephhdiazg/ideal10spring/actions/workflows/test.yml)
[![CI – Docker](https://github.com/josephhdiazg/ideal10spring/actions/workflows/deploy.yml/badge.svg)](https://github.com/josephhdiazg/ideal10spring/actions/workflows/deploy.yml)

</div>

---

## Table of Contents

1. [Team](#team)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Running the Application](#running-the-application)
   - [Docker Compose (recommended)](#docker-compose-recommended)
   - [Local Setup (without Docker)](#local-setup-without-docker)
5. [Environment Variables](#environment-variables)
6. [Test Users](#test-users)
7. [Main Endpoints](#main-endpoints)
8. [Interactive Docs — Swagger UI](#interactive-docs--swagger-ui)
9. [CI / CD](#ci--cd)

---

## Team

| Name | Role |
|---|---|
| Juan González | Developer |
| Michael Arias | Developer |
| Joseph Díaz | Developer |
| Omar Gutiérrez | Developer |

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Backend framework | Spring Boot | 4.0.6 |
| Language | Java | 21 |
| Database | PostgreSQL | 16 |
| Security | Spring Security + JJWT | 0.12.7 |
| DTO mapping | MapStruct | 1.6.3 |
| Env management | Spring Dotenv | 5.1.0 |
| API docs | Springdoc OpenAPI | 2.8.5 |
| Frontend framework | Vue 3 | 3.5.x |
| Build tool | Vite | 7.x |
| Styling | Tailwind CSS + Flowbite | 3.4 / 4.0 |
| Containers | Docker + Docker Compose | — |

---

## Prerequisites

- **Docker** and **Docker Compose** — for the recommended Compose setup
- **Or:** Java 21, Maven 3.9+, Node.js 20+, and PostgreSQL 16 — for local setup

---

## Running the Application

### Docker Compose (recommended)

```bash
# 1. Clone the repository
git clone https://github.com/josephhdiazg/ideal10spring.git
cd ideal10spring

# 2. Set up environment variables
cp .env.example .env
# Edit .env with real values (see Environment Variables section)

# 3. Start all services
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend (Vue) | http://localhost:5173 |
| Backend (API) | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

```bash
# Stop services
docker compose down

# Stop and remove volumes (resets the database)
docker compose down -v
```

---

### Local Setup (without Docker)

#### Backend

```bash
# 1. Make sure PostgreSQL is running and the database exists
createdb ideal10spring

# 2. Configure environment variables
cp .env.example .env
# Edit .env with your local connection details

# 3. Run the backend
./mvnw spring-boot:run
```

API available at `http://localhost:8080`.

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend available at `http://localhost:5173`.

> The frontend proxies `/api/*` requests to `http://localhost:8080`. If you change the backend port, update `frontend/vite.config.js` accordingly.

---

## Environment Variables

Copy `.env.example` to `.env` and fill in the values:

```bash
cp .env.example .env
```

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/ideal10spring` |
| `DB_USERNAME` | PostgreSQL username | `ideal10spring` |
| `DB_PASSWORD` | PostgreSQL password | `ideal10spring` |
| `DB_DRIVER` | JDBC driver class | `org.postgresql.Driver` |
| `JWT_SECRET` | Base64-encoded HMAC secret (min. 32 bytes) | *(generate with command below)* |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | `86400000` *(24 h)* |
| `SPRING_JPA_SHOW_SQL` | Print SQL to console | `false` |

**Generate a secure `JWT_SECRET`:**

```bash
openssl rand -base64 32
```

> **Warning:** Never commit the `.env` file. It is already listed in `.gitignore`.

---

## Test Users

The application seeds one user per role on startup (development only):

| Email | Password | Role |
|---|---|---|
| `admin@ideal10.com` | `admin123` | `ADMINISTRADOR` |
| `hacienda@ideal10.com` | `hacienda123` | `FUNCIONARIO_HACIENDA` |
| `tesoreria@ideal10.com` | `tesoreria123` | `TESORERIA` |
| `contribuyente@ideal10.com` | `contribuyente123` | `CONTRIBUYENTE` |

---

## Main Endpoints

Base path: `/api/v1`. All protected endpoints require:

```
Authorization: Bearer <token>
```

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Sign in and receive a JWT |
| `GET` | `/api/v1/auth/me` | Authenticated | Get current user info |

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@ideal10.com",
  "password": "admin123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin@ideal10.com",
  "roles": ["ROLE_ADMINISTRADOR"]
}
```

---

### Properties

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| `GET` | `/api/v1/properties` | All authenticated | List properties |
| `GET` | `/api/v1/properties/{id}` | All authenticated | Get property |
| `POST` | `/api/v1/properties` | ADMINISTRADOR, HACIENDA | Create property |
| `PUT` | `/api/v1/properties/{id}` | ADMINISTRADOR, HACIENDA | Update property |
| `DELETE` | `/api/v1/properties/{id}` | Authenticated | Delete property |
| `GET` | `/api/v1/properties/{id}/owners` | All authenticated | List owners of a property |
| `POST` | `/api/v1/properties/{id}/owners` | ADMINISTRADOR, HACIENDA | Assign owner to property |
| `PUT` | `/api/v1/properties/{id}/owners/{ownerId}` | ADMINISTRADOR, HACIENDA | Update owner assignment |
| `DELETE` | `/api/v1/properties/{id}/owners/{ownerId}` | Authenticated | Remove owner |

---

### Assessments (Liquidations)

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| `GET` | `/api/v1/liquidations` | All authenticated | List assessments |
| `GET` | `/api/v1/liquidations/{id}` | All authenticated | Get assessment |
| `POST` | `/api/v1/liquidations` | ADMINISTRADOR, HACIENDA, CONTRIBUYENTE | Create assessment |
| `GET` | `/api/v1/liquidations/{id}/payments` | All authenticated | List payments |
| `POST` | `/api/v1/liquidations/{id}/payments` | ADMINISTRADOR, HACIENDA, CONTRIBUYENTE | Register payment |
| `POST` | `/api/v1/liquidations/{id}/clearance-certificates` | ADMINISTRADOR, TESORERIA | Issue clearance certificate |

---

### Fiscal Configuration

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| `GET` | `/api/v1/fiscal-years` | ADMINISTRADOR, TESORERIA | List fiscal years |
| `GET` | `/api/v1/fiscal-years/active` | ADMINISTRADOR, TESORERIA | Get active fiscal year |
| `POST` | `/api/v1/fiscal-years` | ADMINISTRADOR, TESORERIA | Create fiscal year |
| `PUT` | `/api/v1/fiscal-years/{id}` | ADMINISTRADOR, TESORERIA | Update fiscal year |
| `DELETE` | `/api/v1/fiscal-years/{id}` | ADMINISTRADOR, TESORERIA | Delete fiscal year |
| `GET` | `/api/v1/tax-rates` | ADMINISTRADOR, TESORERIA | List tax rates |
| `GET` | `/api/v1/tax-rates/fiscal-year/{id}` | ADMINISTRADOR, TESORERIA | Rates by fiscal year |
| `POST` | `/api/v1/tax-rates` | ADMINISTRADOR, TESORERIA | Create tax rate |
| `PUT` | `/api/v1/tax-rates/{id}` | ADMINISTRADOR, TESORERIA | Update tax rate |
| `GET` | `/api/v1/tax-benefits` | ADMINISTRADOR, TESORERIA | List tax benefits |
| `GET` | `/api/v1/tax-benefits/by-classification` | ADMINISTRADOR, TESORERIA | Benefits by property class |
| `POST` | `/api/v1/tax-benefits` | ADMINISTRADOR, TESORERIA | Create tax benefit |
| `PUT` | `/api/v1/tax-benefits/{id}` | ADMINISTRADOR, TESORERIA | Update tax benefit |
| `GET` | `/api/v1/charge-types` | ADMINISTRADOR, TESORERIA | List charge types |
| `POST` | `/api/v1/charge-types` | ADMINISTRADOR, TESORERIA | Create charge type |
| `PUT` | `/api/v1/charge-types/{id}` | ADMINISTRADOR, TESORERIA | Update charge type |

---

### Clearance Certificates

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| `GET` | `/api/v1/clearance-certificates` | ADMINISTRADOR, TESORERIA | List certificates |
| `GET` | `/api/v1/clearance-certificates/{id}` | ADMINISTRADOR, TESORERIA | Get certificate |

---

### Other Modules

| Method | Endpoint | Description |
|---|---|---|
| `GET/POST/PUT/DELETE` | `/api/v1/owners/**` | Owner management |
| `GET/POST/PUT/DELETE` | `/api/v1/municipalities/**` | Municipality management |
| `GET` | `/api/v1/dashboard/predial` | System metrics |
| `GET` | `/api/v1/audit/**` | Audit log *(ADMINISTRADOR only)* |
| `GET/POST/PUT/DELETE` | `/api/v1/users/**` | User management *(ADMINISTRADOR only)* |

---

## Interactive Docs — Swagger UI

With the application running, open:

```
http://localhost:8080/swagger-ui.html
```

To test protected endpoints, click **Authorize** and enter your token:

```
Bearer eyJhbGciOiJIUzI1NiJ9...
```

The raw OpenAPI spec is available at `http://localhost:8080/api-docs`.

---

## CI / CD

Two GitHub Actions workflows are defined under `.github/workflows/`:

| Workflow | File | Trigger | Purpose |
|---|---|---|---|
| **Test** | `test.yml` | Push / PR to `main` or `develop` | Runs `./mvnw test` on Java 21 |
| **Deploy** | `deploy.yml` | Push to `main` or `develop` | Builds and publishes Docker images to GitHub Container Registry |

Published images:

```
ghcr.io/josephhdiazg/ideal10spring/backend:<branch|sha>
ghcr.io/josephhdiazg/ideal10spring/frontend:<branch|sha>
```
