import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useEffect } from 'react';
import { BrowserRouter, Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { ActiveSessionPage } from './pages/ActiveSessionPage';
import { SessionSetPage } from './pages/SessionSetPage';
import { ExerciseFormPage } from './pages/ExerciseFormPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { LoginPage } from './pages/LoginPage';
import { PlanDetailPage } from './pages/PlanDetailPage';
import { PlanFormPage } from './pages/PlanFormPage';
import { PlansListPage } from './pages/PlansListPage';
import { RegisterPage } from './pages/RegisterPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { SessionReviewPage } from './pages/SessionReviewPage';
import { WorkoutFormPage } from './pages/WorkoutFormPage';
import { useAuth } from './stores/auth';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function ProtectedRoute() {
  const token = useAuth((s) => s.token);
  if (!token) return <Navigate to="/login" replace />;
  return <Outlet />;
}

function PublicRoute() {
  const token = useAuth((s) => s.token);
  if (token) return <Navigate to="/" replace />;
  return <Outlet />;
}

function ThemeBootstrap() {
  useEffect(() => {
    const stored = localStorage.getItem('theme');
    const dark = stored
      ? stored === 'dark'
      : window.matchMedia('(prefers-color-scheme: dark)').matches;
    document.documentElement.classList.toggle('dark', dark);
  }, []);
  return null;
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ThemeBootstrap />
        <Routes>
          <Route element={<PublicRoute />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          </Route>
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<PlansListPage />} />
            <Route path="/plans/:id" element={<PlanDetailPage />} />
            <Route path="/plans/new" element={<PlanFormPage />} />
            <Route path="/plans/:id/edit" element={<PlanFormPage />} />
            <Route
              path="/plans/:planId/workouts/:workoutId"
              element={<WorkoutFormPage />}
            />
            <Route
              path="/plans/:planId/workouts/:workoutId/edit"
              element={<WorkoutFormPage />}
            />
            <Route
              path="/plans/:planId/workouts/:workoutId/exercises/:exerciseId"
              element={<ExerciseFormPage />}
            />
            <Route path="/sessions/:sessionId" element={<ActiveSessionPage />} />
            <Route
              path="/sessions/:sessionId/exercises/:exerciseId/sets/:setIndex"
              element={<SessionSetPage />}
            />
            <Route path="/sessions/:sessionId/review" element={<SessionReviewPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
