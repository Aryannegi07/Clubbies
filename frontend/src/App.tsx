import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { Layout, Protected } from './components/Layout';
import { LandingPage } from './pages/LandingPage';
import { ClubsPage } from './pages/ClubsPage';
import { ClubPage } from './pages/ClubPage';
import { EventsPage } from './pages/EventsPage';
import { EventPage } from './pages/EventPage';
import { AuthPage } from './pages/AuthPage';
import { DashboardPage } from './pages/DashboardPage';
import { PresidentCupPage } from './pages/PresidentCupPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { AdminPage } from './pages/AdminPage';
import { CorePage } from './pages/CorePage';

const queryClient = new QueryClient({ defaultOptions: { queries: { staleTime: 30_000, retry: 1 } } });
export default function App() { return <QueryClientProvider client={queryClient}><AuthProvider><BrowserRouter><Routes><Route element={<Layout />}><Route index element={<LandingPage />} /><Route path="clubs" element={<ClubsPage />} /><Route path="clubs/:id" element={<ClubPage />} /><Route path="events" element={<EventsPage />} /><Route path="events/:id" element={<EventPage />} /><Route path="president-cup" element={<PresidentCupPage />} /><Route path="auth" element={<AuthPage />} /><Route path="dashboard" element={<Protected><DashboardPage /></Protected>} /><Route path="notifications" element={<Protected><NotificationsPage /></Protected>} /><Route path="admin" element={<Protected><AdminPage /></Protected>} /><Route path="core" element={<Protected><CorePage /></Protected>} /><Route path="*" element={<Navigate to="/" replace />} /></Route></Routes></BrowserRouter></AuthProvider></QueryClientProvider>; }
