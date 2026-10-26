import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { endpoints } from '../lib/api';
import type { User } from '../types';

interface AuthValue { user: User | null; loading: boolean; error: string | null; login: (email: string, password: string) => Promise<void>; register: (name: string, email: string, password: string) => Promise<void>; logout: () => Promise<void>; }
const AuthContext = createContext<AuthValue | undefined>(undefined);
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null); const [loading, setLoading] = useState(true); const [error, setError] = useState<string | null>(null);
  useEffect(() => { endpoints.me().then(setUser).catch(() => setUser(null)).finally(() => setLoading(false)); }, []);
  const value = useMemo<AuthValue>(() => ({ user, loading, error,
    async login(email, password) { setError(null); try { setUser(await endpoints.login({ email, password })); } catch (e) { const message = e instanceof Error ? e.message : 'Unable to sign in'; setError(message); throw e; } },
    async register(name, email, password) { setError(null); try { setUser(await endpoints.register({ displayName: name, email, password })); } catch (e) { const message = e instanceof Error ? e.message : 'Unable to register'; setError(message); throw e; } },
    async logout() { await endpoints.logout().catch(() => undefined); setUser(null); }
  }), [user, loading, error]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export function useAuth() { const context = useContext(AuthContext); if (!context) throw new Error('useAuth must be used inside AuthProvider'); return context; }
