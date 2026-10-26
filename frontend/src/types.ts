export type Role = 'SUPER_ADMIN' | 'CLUB_CORE' | 'STUDENT';
export interface User { id: string; email: string; displayName: string; status: string; roles: Role[]; }
export interface Club { id: string; name: string; fullName?: string; description: string; category?: string; logoUrl?: string; contactEmail?: string; recruitmentOpen: boolean; active: boolean; }
export interface Event { id: string; clubId: string; title: string; description: string; category?: string; startsAt: string; endsAt: string; venue?: string; expectedAttendance?: number; coverImageUrl?: string; registrationCapacity?: number; status: string; approvalStatus: string; }
export interface Membership { id: string; clubId: string; userId: string; status: string; appliedAt: string; decidedAt?: string; }
export interface Drive { id: string; clubId: string; title: string; description: string; positions: string; eligibility?: string; opensAt: string; closesAt: string; status: string; }
export interface Application { id: string; driveId: string; applicantId: string; answersJson: string; status: string; submittedAt: string; reviewerNotes?: string; }
export interface Edition { id: string; name: string; introduction?: string; rules?: string; registrationOpensAt?: string; registrationClosesAt?: string; status: string; }
export interface Sport { id: string; editionId: string; name: string; description?: string; }
export interface Competition { id: string; sportId: string; name: string; participationType: string; minTeamSize?: number; maxTeamSize?: number; participantLimit?: number; opensAt?: string; closesAt?: string; status: string; }
export interface Registration { id: string; competitionId: string; participantId: string; teamId?: string; status: string; createdAt: string; }
export interface Notification { id: string; title: string; message: string; targetUrl?: string; readAt?: string; createdAt: string; }
