import { LogOut, Moon, Sun } from 'lucide-react';
import { useEffect, useState, type ReactNode } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/stores/auth';
import { Button } from './ui/button';

interface Props {
  title?: string;
  back?: string;
  actions?: ReactNode;
  children: ReactNode;
}

function useDarkMode() {
  const [dark, setDark] = useState<boolean>(() => {
    if (typeof window === 'undefined') return false;
    const stored = localStorage.getItem('theme');
    if (stored) return stored === 'dark';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  });

  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark);
    localStorage.setItem('theme', dark ? 'dark' : 'light');
  }, [dark]);

  return { dark, toggle: () => setDark((v) => !v) };
}

export function Layout({ title, back, actions, children }: Props) {
  const navigate = useNavigate();
  const user = useAuth((s) => s.user);
  const logout = useAuth((s) => s.logout);
  const { dark, toggle } = useDarkMode();

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-10 border-b border-border bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto flex h-14 max-w-3xl items-center gap-3 px-4">
          {back ? (
            <Button variant="ghost" size="icon" onClick={() => navigate(back)} aria-label="Voltar">
              <span className="text-xl">‹</span>
            </Button>
          ) : (
            <Link to="/" className="font-bold tracking-tight">
              Gym Tracker
            </Link>
          )}
          <h1 className="flex-1 truncate text-base font-semibold">{title}</h1>
          {actions}
          <Button variant="ghost" size="icon" onClick={toggle} aria-label="Alternar tema">
            {dark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          </Button>
          {user && (
            <Button variant="ghost" size="icon" onClick={logout} aria-label="Sair">
              <LogOut className="h-4 w-4" />
            </Button>
          )}
        </div>
      </header>
      <main className="mx-auto max-w-3xl px-4 py-6">{children}</main>
    </div>
  );
}
