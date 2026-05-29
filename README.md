# ideal10spring

## Local configuration

Runtime database credentials must not be committed to Git. Copy `.env.example`
to `.env` and replace the placeholder values with your local database settings.
Set `JWT_SECRET` to a base64-encoded 32-byte secret for local token signing.

Spring Dotenv loads `.env` automatically when the application starts. Real
environment variables take precedence over values in `.env`.

## GitHub Actions

This repository includes two workflows under `.github/workflows`:

| Workflow | File | Triggers | Purpose |
| --- | --- | --- | --- |
| `test` | `.github/workflows/test.yml` | Pushes and pull requests targeting `main` or `develop`, plus manual runs | Sets up Java 21 and runs `./mvnw test` with Maven dependency caching. |
| `deploy` | `.github/workflows/deploy.yml` | Pushes to `main` or `develop`, plus manual runs | Builds and publishes separate backend and frontend Docker images to GitHub Container Registry. |

The deploy workflow uses the repository `GITHUB_TOKEN` with package write
permissions to push images to GitHub Container Registry:

- `ghcr.io/${{ github.repository }}/backend`
- `ghcr.io/${{ github.repository }}/frontend`

## Docker Compose

The local Compose stack defines separate services for the Spring backend, Vite
frontend, and PostgreSQL database:

- `backend` builds `ideal10spring-backend:local` from the root `Dockerfile` and
  listens on port `8080`.
- `frontend` builds `ideal10spring-frontend:local` from `frontend/Dockerfile`,
  serves the Vite production build through nginx, proxies `/api/*` requests to
  the `backend` service, and is exposed on port `5173`.
- `db` runs PostgreSQL 16 and is used by the backend through the internal
  Compose hostname `db`.

## Test users

The application seeds one user per role on startup:

| Username | Password | Role |
| --- | --- | --- |
| `admin@ideal10.com` | `admin123` | `ROLE_ADMINISTRADOR` |
| `hacienda@ideal10.com` | `hacienda123` | `ROLE_FUNCIONARIO_HACIENDA` |
| `tesoreria@ideal10.com` | `tesoreria123` | `ROLE_TESORERIA` |
| `contribuyente@ideal10.com` | `contribuyente123` | `ROLE_CONTRIBUYENTE` |

Login endpoint:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@ideal10.com",
  "password": "admin123"
}
```
