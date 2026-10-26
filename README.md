# Clubbies

**One Campus. Every Club. One Platform.**

Clubbies is a modular monolith for college club discovery, membership and recruitment workflows, event publishing, room booking, and President Cup sports registrations.

## Current implementation

- React 18 + TypeScript + Vite frontend.
- Java 21 + Spring Boot 3.4 + Spring Security + Spring Data JPA backend.
- PostgreSQL schema managed by Flyway.
- Server-managed session authentication with HttpOnly cookies.
- Exactly three application roles: `SUPER_ADMIN`, `CLUB_CORE`, and `STUDENT`.
- Idempotent seed for IEEE, Aryabhat, and Wizard only.
- Club discovery, membership applications, recruitment drives, event workflow, room availability/booking, President Cup configuration and registrations, notifications, and audit logging foundations.

## Repository structure

```text
frontend/              React application
backend/               Spring Boot REST API
docs/                   Audit, architecture, schema, API, security, deployment
```

## Prerequisites

- Java 21 LTS
- Maven 3.9+
- Node.js 20+ and npm 10+ (the current worker has Node 24 but its npm installation is damaged; repair/reinstall npm before running frontend commands)
- PostgreSQL 15+ (Neon works for deployment)

## Local configuration

Copy the examples and set real values locally. Spring Boot does not automatically load a `.env` file, so export the variables in the PowerShell session that starts the backend:

```powershell
Copy-Item backend/.env.example backend/.env
Copy-Item frontend/.env.example frontend/.env

$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/Clubbies"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "your-local-password"
$env:SERVER_PORT = "8081"
$env:FRONTEND_ORIGIN = "http://localhost:5173"
```

Important backend variables:

| Variable | Purpose |
| --- | --- |
| `DATABASE_URL` | JDBC PostgreSQL URL, e.g. `jdbc:postgresql://localhost:5432/clubbies` |
| `DATABASE_USERNAME` | Database user |
| `DATABASE_PASSWORD` | Database password |
| `FRONTEND_ORIGIN` | Allowed browser origin |
| `ADMIN_BOOTSTRAP_EMAIL` | Optional email for first-admin bootstrap |
| `ADMIN_BOOTSTRAP_PASSWORD` | Optional password for first-admin bootstrap; never commit it |
| `ADMIN_BOOTSTRAP_NAME` | Optional display name |

## Database and run commands

Create an empty PostgreSQL database, then run the API. Flyway applies migrations on startup; the seed runner inserts the three initial clubs idempotently.

```powershell
cd backend
mvn spring-boot:run
```

The API listens on `http://localhost:8081` by default. Health: `GET /api/health`.

The first admin is provisioned only when bootstrap credentials are supplied to the backend process:

```powershell
$env:ADMIN_BOOTSTRAP_EMAIL = "admin@example.edu"
$env:ADMIN_BOOTSTRAP_PASSWORD = "use-a-long-local-password"
$env:ADMIN_BOOTSTRAP_NAME = "Platform Administrator"
cd backend
mvn spring-boot:run
```

After one successful bootstrap, remove the password from the environment. Public registration never creates an administrator.

## Frontend

```powershell
cd frontend
npm install
npm run dev
```

Set `VITE_API_BASE_URL=http://localhost:8081/api` in `frontend/.env` when the API is not proxied.

Restart Vite after changing `.env`; Vite reads environment variables when it starts.

## Checks

```powershell
cd backend
mvn test
mvn package

cd ..\frontend
npm run typecheck
npm run build
```

The exact checks run in this workspace and any environment limitations are recorded in `docs/DEPLOYMENT.md`.

## Deployment

See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md), [docs/SECURITY.md](docs/SECURITY.md), and [docs/API.md](docs/API.md) for Render, Vercel, Neon, configuration, and API details.

For an honest feature-by-feature boundary, see [docs/IMPLEMENTATION_STATUS.md](docs/IMPLEMENTATION_STATUS.md).
