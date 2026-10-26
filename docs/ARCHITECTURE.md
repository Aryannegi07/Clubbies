# Architecture

Clubbies is a modular monolith. The React/Vite frontend communicates with a single Spring Boot REST API. PostgreSQL is the source of truth; Flyway owns schema evolution and JPA uses `ddl-auto=validate` outside tests.

## Backend boundaries

- `auth`: registration, session login/logout, current-user profile, first-admin bootstrap.
- `clubs` and `memberships`: public discovery, applications, core assignments, activity logging.
- `recruitment`: drive lifecycle and scoped applications.
- `events`: draft/submission/approval/publication lifecycle.
- `rooms` and `bookings`: timetable-aware availability and transactional requests.
- `presidentcup`: editions, sports, competitions, registrations and teams.
- `notifications` and `audit`: user notifications and append-only administrative records.

Controllers expose DTOs only. Services enforce business rules and authorization. Repositories contain persistence queries. `SecurityConfig` uses a database-backed `UserDetailsService` and server-managed HTTP sessions.

## Frontend boundaries

The Vite application has shared layout/UI primitives, typed API functions, an auth context, and feature-oriented route pages. TanStack Query owns server state while React Hook Form/Zod are used at form boundaries. The UI is intentionally useful in an empty database: public clubs and events show empty states, and tournament copy is labelled as configurable rather than factual.

## Data flow

1. Browser sends JSON over HTTPS to `/api`.
2. Spring Security authenticates the session cookie and checks role/club assignment.
3. DTO validation runs at the edge.
4. Service methods run transactional workflows.
5. PostgreSQL constraints and indexes protect uniqueness and integrity.
6. JSON DTOs return only permitted fields.

The college timezone is `Asia/Kolkata`; event and booking timestamps are stored as timezone-aware values.
