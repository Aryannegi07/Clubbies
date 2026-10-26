# Security model

- Passwords are hashed with Spring Security's Argon2 encoder.
- Public registration always creates `STUDENT`; role and club assignments are never accepted from the request body.
- Login uses Spring Security's server-managed session. Cookies are HttpOnly, SameSite=Lax, and Secure in production.
- CORS allows only `FRONTEND_ORIGIN`.
- Sensitive endpoints use role guards plus service-level club assignment checks.
- DTOs prevent mass assignment and entities are never serialized directly.
- Bean Validation rejects malformed payloads; database constraints backstop uniqueness and foreign keys.
- Booking approval runs transactionally and confirmed reservations use PostgreSQL range-overlap protection.
- Audit records omit passwords, sessions, reset tokens, and unnecessary personal data.
- Admin bootstrap requires externally supplied environment credentials and is disabled unless explicitly configured.

This is an implementation baseline, not a claim of complete security. Production deployment still requires HTTPS, secret rotation, managed database access control, monitoring, rate-limit tuning, and a focused penetration review.
