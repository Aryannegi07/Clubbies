# Deployment

## Neon PostgreSQL

Create a Neon database and provide its pooled or direct JDBC URL as `DATABASE_URL`, with `sslmode=require` in the query string when required by the chosen Neon endpoint. Use a dedicated least-privilege application role. Do not expose this URL to Vite.

## Render backend

Use the repository root with backend working directory `backend` (or configure the Render root directory accordingly). `render.yaml` and `backend/Dockerfile` are included as starting configuration; review the selected Render plan and runtime before provisioning.

- Build: `mvn -DskipTests package`
- Start: `java -jar target/clubbies-api-0.1.0.jar`
- Health check: `/api/health`
- Required variables: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `FRONTEND_ORIGIN`, `SPRING_PROFILES_ACTIVE=prod`

Flyway runs on application startup. Render must be configured with a persistent environment and the same schema owner used for migrations.

## Vercel frontend

Set project root to `frontend`, build command `npm run build`, output directory `dist`, and `VITE_API_BASE_URL` to the Render API origin plus `/api`. Add an SPA rewrite in `vercel.json` so React Router routes resolve to `index.html`.

## Media

Cloudinary integration points are documented in configuration but are optional until credentials are supplied. The core app uses validated URL fields for logos/posters and remains functional without media credentials.

## Checks run in this workspace

- Java 21.0.10 and Maven 3.9.16 are available.
- PostgreSQL client 18.4 is available and `pg_isready` reports a local server accepting connections, but the worker did not have its database credentials. `mvn spring-boot:run` reached Flyway and then failed with `FATAL: password authentication failed for user "clubbies"`; `psql -h localhost -U postgres -w` also could not authenticate. Docker is unavailable.

The repository `.env` files are examples for humans and are not loaded automatically by Spring Boot. On Windows, export `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `SERVER_PORT`, and `FRONTEND_ORIGIN` in the same PowerShell session before running Maven.
- Node 24.19.0 is available, but the installed npm launcher fails because its bundled `npm-prefix.js`/`npm-cli.js` paths are broken. Frontend install/build remains pending until npm is repaired or a working package manager is supplied.
