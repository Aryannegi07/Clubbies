# Legacy audit — Clubees.zip

Audit source: `legacy-original/Club-Management-System-main/`, extracted from the supplied `Clubees.zip` before rebuild.

## Verified structure

The archive contains `app.py`, `index.html`, and a bundled Windows Python virtual environment. The application is a Flask API with a single static HTML page. The environment is preserved for recovery but is not part of the rebuilt application.

## Existing implementation

- `app.py` defines a Flask app with permissive `flask_cors.CORS(app)`.
- Data is stored in `.ds_data/users.json`, `clubs.json`, and `events.json`.
- In-memory custom `LinkedList`, `Queue`, and `Stack` classes mirror the JSON collections.
- Routes include `/signup`, `/login`, `/clubs`, `/events`, `/join`, `/remove`, admin routes, and an event-registration queue.
- `index.html` contains all markup, CSS, and JavaScript in one file. It calls `http://127.0.0.1:5000` directly and uses `sessionStorage` for a client-side display session.

## Verified seeded content

`init_defaults()` seeds `SWARAGINI`, `ARYAVART`, `WIZARD`, and `THE CREW`, plus two simple events. This rebuild intentionally replaces that seed with exactly IEEE, Aryabhat, and Wizard as requested.

## Important weaknesses

1. Passwords are stored and compared in plaintext (`app.py:184-197`).
2. Admin credentials are hardcoded as `admin` / `admin123` (`app.py:351-362` and displayed in the page).
3. There are no authenticated server sessions or server-side authorization checks.
4. Several mutation routes trust a caller-supplied `name`; a caller can act on another account by changing JSON input.
5. `/admin/users` returns every user object, including plaintext passwords.
6. CORS is unrestricted and the API has no CSRF, rate limiting, account status, or audit controls.
7. JSON read-modify-write persistence has no transaction or concurrency protection.
8. Event registration only appends to an in-memory queue; it does not persist or prevent duplicates.
9. Delete operations can remove clubs/events without relational integrity or recovery beyond an in-memory undo stack.
10. The UI is a single static document with inline handlers, browser alerts, client-only role state, no typed API boundary, and no loading/error/accessibility system.
11. The page clears `sessionStorage` at startup (`index.html:989-999`), so sessions never persist across reloads.
12. The legacy app has no real room scheduling, recruitment, notifications, President Cup, migrations, tests, or deployment configuration.

## Preserved and rebuilt ideas

The rebuild preserves club discovery, user registration/login, joining clubs, event discovery, and administrative club/event management concepts. These are implemented as typed REST workflows with PostgreSQL persistence, role/club authorization, server validation, migrations, and a React interface. Legacy source remains recoverable under `legacy-original/`.
