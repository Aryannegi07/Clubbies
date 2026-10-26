import type { Application, Club, Competition, Drive, Edition, Event, Membership, Notification, Registration, Sport, User } from '../types';

const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api';
function csrfToken() { return document.cookie.split('; ').find((entry) => entry.startsWith('XSRF-TOKEN='))?.split('=')[1]; }
async function ensureCsrfToken() {
  if (csrfToken()) return;
  await fetch(`${base}/auth/csrf`, { credentials: 'include' });
}
export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  if (init.method && !['GET', 'HEAD', 'OPTIONS'].includes(init.method.toUpperCase())) await ensureCsrfToken();
  const headers = new Headers(init.headers); if (init.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  const csrf = csrfToken(); if (csrf) headers.set('X-XSRF-TOKEN', decodeURIComponent(csrf));
  const response = await fetch(`${base}${path}`, { ...init, headers, credentials: 'include' });
  if (!response.ok) { const body = await response.json().catch(() => null); throw new Error(body?.message || `Request failed (${response.status})`); }
  if (response.status === 204) return undefined as T; return response.json() as Promise<T>;
}
export const endpoints = {
  me: () => api<User>('/auth/me'), login: (body: { email: string; password: string }) => api<User>('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  register: (body: { email: string; password: string; displayName: string }) => api<User>('/auth/register', { method: 'POST', body: JSON.stringify(body) }), logout: () => api<void>('/auth/logout', { method: 'POST' }),
  clubs: (search = '') => api<Club[]>(`/clubs${search ? `?search=${encodeURIComponent(search)}` : ''}`), club: (id: string) => api<Club>(`/clubs/${id}`), applyMembership: (id: string) => api<unknown>(`/clubs/${id}/membership-applications`, { method: 'POST' }), memberships: () => api<Membership[]>('/memberships/me'),
  events: () => api<Event[]>('/events'), event: (id: string) => api<Event>(`/events/${id}`),
  drives: () => api<Drive[]>('/recruitment/drives'), applications: () => api<Application[]>('/recruitment/applications/me'), applyDrive: (id: string, answersJson: string) => api<Application>(`/recruitment/drives/${id}/applications`, { method: 'POST', body: JSON.stringify({ answersJson }) }),
  activeEdition: () => api<Edition | null>('/president-cup/active'), sports: (editionId: string) => api<Sport[]>(`/president-cup/editions/${editionId}/sports`), competitions: (sportId: string) => api<Competition[]>(`/president-cup/sports/${sportId}/competitions`), registerCompetition: (id: string, teamId?: string) => api<Registration>(`/president-cup/competitions/${id}/registrations`, { method: 'POST', body: JSON.stringify({ teamId }) }), registrations: () => api<Registration[]>('/president-cup/registrations/me'),
  notifications: () => api<Notification[]>('/notifications'), readNotification: (id: string) => api<Notification>(`/notifications/${id}/read`, { method: 'POST' }),
};
