# REST API

Base URL: `/api`.

## Public

- `GET /health`
- `GET /clubs?search=&category=`
- `GET /clubs/{id}`
- `GET /events?from=&to=&clubId=` (published events)
- `GET /events/{id}`
- `GET /president-cup/active`
- `GET /president-cup/editions/{id}`

## Authentication

- `POST /auth/register` — student registration, validates email and password.
- `POST /auth/login` — creates a server session and HttpOnly cookie.
- `POST /auth/logout` — invalidates the session.
- `GET /auth/me` — current permitted profile.
- `PATCH /auth/me` — profile update.

## Authenticated workflows

- `POST /clubs/{id}/membership-applications`
- `GET /memberships/me`
- `GET /recruitment/drives`
- `POST /recruitment/drives/{id}/applications`
- `GET /recruitment/applications/me`
- `POST /events` and `PATCH /events/{id}` for assigned core members/admins.
- `GET /rooms/availability?start=&end=&capacity=`
- `GET /rooms` and `POST/PATCH /rooms/{id}` (administrator room catalog)
- `POST /bookings`
- `GET /bookings/mine`
- `GET /notifications`, `POST /notifications/{id}/read`
- `POST /president-cup/competitions/{id}/registrations`
- `POST /president-cup/teams`, `POST /president-cup/teams/{id}/invitations`

## Super Admin

- `POST/PATCH /admin/clubs`
- `POST /admin/clubs/{clubId}/core-members`
- `PATCH /admin/users/{id}/status`
- `POST /admin/bookings/{id}/approve` or `/reject`
- `POST /admin/rooms` and timetable import endpoints.
- `POST/PATCH /admin/president-cup/editions`, sports, competitions, fixtures, results, announcements.
- `GET /admin/audit-logs`

All responses use DTOs and validation errors follow `{ timestamp, status, code, message, fieldErrors }`. Private recruitment notes and other users' applications are never returned to students.
