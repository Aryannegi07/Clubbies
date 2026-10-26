# Database schema

Flyway migration `V1__initial_schema.sql` creates the normalized baseline schema. Key tables include:

- `users`, `user_roles`, `club_core_assignments`, and `user_sessions` for identity and authorization.
- `clubs`, `memberships`, `recruitment_drives`, and `recruitment_applications` for club workflows.
- `events`, `rooms`, `academic_timetable_slots`, `blocked_periods`, `booking_requests`, and `room_reservations` for scheduling.
- `notifications`, `club_activity_logs`, and `platform_audit_logs` for communication and accountability.
- `tournament_editions`, `sports`, `competitions`, `tournament_registrations`, `teams`, `team_members`, `team_invitations`, `fixtures`, and `tournament_announcements` for President Cup.

Unique constraints prevent duplicate emails, club names, active memberships, recruitment applications, and tournament registrations. Foreign keys use restrictive or cascading deletes according to ownership. Booking conflicts are checked inside a transaction and the schema includes a PostgreSQL exclusion constraint for confirmed reservations using `btree_gist` when available.

Room/timetable data is intentionally not fabricated. An empty state is shown until an administrator imports the college dataset.
