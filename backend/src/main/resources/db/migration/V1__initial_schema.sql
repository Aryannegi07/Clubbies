CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE users (
    id uuid PRIMARY KEY,
    email varchar(320) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    display_name varchar(120) NOT NULL,
    status varchar(24) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED','DEACTIVATED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE user_roles (
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role varchar(24) NOT NULL CHECK (role IN ('SUPER_ADMIN','CLUB_CORE','STUDENT')),
    PRIMARY KEY (user_id, role)
);
CREATE TABLE clubs (
    id uuid PRIMARY KEY,
    name varchar(80) NOT NULL UNIQUE,
    full_name varchar(180),
    description varchar(1800) NOT NULL,
    category varchar(80),
    logo_url varchar(1000),
    contact_email varchar(320),
    recruitment_open boolean NOT NULL DEFAULT false,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE club_core_assignments (
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_at timestamptz NOT NULL DEFAULT now(),
    assigned_by uuid REFERENCES users(id),
    PRIMARY KEY (club_id, user_id)
);
CREATE TABLE memberships (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    status varchar(24) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED','WITHDRAWN','INACTIVE')),
    applied_at timestamptz NOT NULL DEFAULT now(),
    decided_at timestamptz,
    decided_by uuid REFERENCES users(id),
    CONSTRAINT uq_membership_user_club UNIQUE (user_id, club_id)
);
CREATE INDEX idx_memberships_club_status ON memberships(club_id, status);

CREATE TABLE recruitment_drives (
    id uuid PRIMARY KEY,
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    title varchar(160) NOT NULL,
    description varchar(3000) NOT NULL,
    positions varchar(1200) NOT NULL,
    eligibility varchar(1800),
    opens_at timestamptz NOT NULL,
    closes_at timestamptz NOT NULL,
    status varchar(24) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','OPEN','CLOSED')),
    created_by uuid NOT NULL REFERENCES users(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CHECK (closes_at > opens_at)
);
CREATE TABLE recruitment_applications (
    id uuid PRIMARY KEY,
    drive_id uuid NOT NULL REFERENCES recruitment_drives(id) ON DELETE CASCADE,
    applicant_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    answers_json varchar(10000) NOT NULL DEFAULT '{}',
    reviewer_notes varchar(4000),
    status varchar(24) NOT NULL DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED','UNDER_REVIEW','SHORTLISTED','SELECTED','REJECTED','WITHDRAWN')),
    submitted_at timestamptz NOT NULL DEFAULT now(),
    reviewed_at timestamptz,
    reviewed_by uuid REFERENCES users(id),
    CONSTRAINT uq_recruitment_applicant_drive UNIQUE (drive_id, applicant_id)
);

CREATE TABLE events (
    id uuid PRIMARY KEY,
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE RESTRICT,
    title varchar(180) NOT NULL,
    description varchar(4000) NOT NULL,
    category varchar(80),
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    venue varchar(180),
    expected_attendance integer CHECK (expected_attendance IS NULL OR expected_attendance > 0),
    cover_image_url varchar(1000),
    registration_capacity integer CHECK (registration_capacity IS NULL OR registration_capacity > 0),
    status varchar(24) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','SUBMITTED','APPROVED','REJECTED','PUBLISHED','COMPLETED','POSTPONED','CANCELLED')),
    approval_status varchar(24) NOT NULL DEFAULT 'PENDING',
    created_by uuid NOT NULL REFERENCES users(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CHECK (ends_at > starts_at)
);
CREATE INDEX idx_events_public_start ON events(status, starts_at);

CREATE TABLE rooms (
    id uuid PRIMARY KEY,
    name varchar(80) NOT NULL UNIQUE,
    room_type varchar(60),
    capacity integer NOT NULL CHECK (capacity > 0),
    facilities varchar(1000),
    active boolean NOT NULL DEFAULT true
);
CREATE TABLE academic_timetable_slots (
    id uuid PRIMARY KEY,
    room_id uuid NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    label varchar(180),
    CHECK (ends_at > starts_at)
);
CREATE TABLE blocked_periods (
    id uuid PRIMARY KEY,
    room_id uuid NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    reason varchar(500),
    CHECK (ends_at > starts_at)
);
CREATE TABLE booking_requests (
    id uuid PRIMARY KEY,
    room_id uuid NOT NULL REFERENCES rooms(id) ON DELETE RESTRICT,
    event_id uuid REFERENCES events(id) ON DELETE SET NULL,
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE RESTRICT,
    requested_by uuid NOT NULL REFERENCES users(id),
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    status varchar(24) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED')),
    notes varchar(1200),
    reviewed_by uuid REFERENCES users(id),
    reviewed_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    CHECK (ends_at > starts_at)
);
CREATE TABLE room_reservations (
    id uuid PRIMARY KEY,
    room_id uuid NOT NULL REFERENCES rooms(id) ON DELETE RESTRICT,
    booking_request_id uuid NOT NULL UNIQUE REFERENCES booking_requests(id) ON DELETE CASCADE,
    starts_at timestamptz NOT NULL,
    ends_at timestamptz NOT NULL,
    reservation_window tstzrange GENERATED ALWAYS AS (tstzrange(starts_at, ends_at, '[)')) STORED,
    EXCLUDE USING gist (room_id WITH =, reservation_window WITH &&)
);

CREATE TABLE notifications (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title varchar(180) NOT NULL,
    message varchar(1200) NOT NULL,
    target_url varchar(500),
    read_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX idx_notifications_user ON notifications(user_id, created_at DESC);
CREATE TABLE club_activity_logs (
    id uuid PRIMARY KEY,
    club_id uuid NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    actor_id uuid NOT NULL REFERENCES users(id),
    action varchar(100) NOT NULL,
    target_id uuid,
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE platform_audit_logs (
    id uuid PRIMARY KEY,
    actor_id uuid REFERENCES users(id),
    action varchar(100) NOT NULL,
    target_type varchar(80),
    target_id uuid,
    outcome varchar(24) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE tournament_editions (
    id uuid PRIMARY KEY,
    name varchar(160) NOT NULL UNIQUE,
    introduction varchar(4000),
    rules varchar(4000),
    registration_opens_at timestamptz,
    registration_closes_at timestamptz,
    status varchar(24) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','ACTIVE','CLOSED','CANCELLED')),
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE sports (
    id uuid PRIMARY KEY,
    edition_id uuid NOT NULL REFERENCES tournament_editions(id) ON DELETE CASCADE,
    name varchar(120) NOT NULL,
    description varchar(1000),
    active boolean NOT NULL DEFAULT true
);
CREATE TABLE competitions (
    id uuid PRIMARY KEY,
    sport_id uuid NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    name varchar(160) NOT NULL,
    participation_type varchar(16) NOT NULL DEFAULT 'INDIVIDUAL' CHECK (participation_type IN ('INDIVIDUAL','TEAM')),
    min_team_size integer,
    max_team_size integer,
    participant_limit integer,
    opens_at timestamptz,
    closes_at timestamptz,
    status varchar(24) NOT NULL DEFAULT 'DRAFT'
);
CREATE TABLE teams (
    id uuid PRIMARY KEY,
    competition_id uuid NOT NULL REFERENCES competitions(id) ON DELETE CASCADE,
    name varchar(120) NOT NULL,
    captain_id uuid NOT NULL REFERENCES users(id),
    locked boolean NOT NULL DEFAULT false
);
CREATE TABLE team_members (
    team_id uuid NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY(team_id, user_id)
);
CREATE TABLE team_invitations (
    id uuid PRIMARY KEY,
    team_id uuid NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    invitee_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(team_id, invitee_id)
);
CREATE TABLE tournament_registrations (
    id uuid PRIMARY KEY,
    competition_id uuid NOT NULL REFERENCES competitions(id) ON DELETE CASCADE,
    participant_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id uuid REFERENCES teams(id) ON DELETE SET NULL,
    status varchar(24) NOT NULL DEFAULT 'SUBMITTED',
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT uq_competition_participant UNIQUE (competition_id, participant_id)
);
CREATE TABLE fixtures (
    id uuid PRIMARY KEY,
    competition_id uuid NOT NULL REFERENCES competitions(id) ON DELETE CASCADE,
    home_label varchar(160) NOT NULL,
    away_label varchar(160) NOT NULL,
    starts_at timestamptz,
    venue varchar(180),
    home_score integer,
    away_score integer,
    status varchar(24) NOT NULL DEFAULT 'SCHEDULED',
    published boolean NOT NULL DEFAULT false
);
CREATE TABLE tournament_announcements (
    id uuid PRIMARY KEY,
    edition_id uuid NOT NULL REFERENCES tournament_editions(id) ON DELETE CASCADE,
    title varchar(180) NOT NULL,
    body varchar(4000) NOT NULL,
    published boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE user_sessions (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash varchar(128) NOT NULL UNIQUE,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz
);
