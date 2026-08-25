import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useLogin } from '@/lib/api/hooks';
import { extractApiError } from '@/lib/api/client';
import { useAuth } from '@/stores/auth';

const schema = z.object({
  email: z.string().email('E-mail inválido'),
  password: z.string().min(1, 'Obrigatório'),
});

type FormData = z.infer<typeof schema>;

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const notice = (location.state as { notice?: string } | null)?.notice;
  const setAuth = useAuth((s) => s.setAuth);
  const login = useLogin();
  const [formError, setFormError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (data) => {
    setFormError(null);
    try {
      const res = await login.mutateAsync(data);
      setAuth(res.token, res.user);
      navigate('/');
    } catch (e) {
      setFormError(extractApiError(e));
    }
  });

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold tracking-tight">Gym Tracker</h1>
          <p className="mt-2 text-sm text-muted-foreground">Entre na sua conta</p>
        </div>
        <form onSubmit={onSubmit} className="space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="email">E-mail</Label>
            <Input id="email" type="email" autoComplete="email" {...register('email')} />
            {errors.email && (
              <p className="text-xs text-destructive">{errors.email.message}</p>
            )}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="password">Senha</Label>
            <Input
              id="password"
              type="password"
              autoComplete="current-password"
              {...register('password')}
            />
            {errors.password && (
              <p className="text-xs text-destructive">{errors.password.message}</p>
            )}
            <p className="text-right">
              <Link to="/forgot-password" className="text-xs font-medium text-primary hover:underline">
                Esqueceu a senha?
              </Link>
            </p>
          </div>
          {notice && (
            <p className="rounded-xl border border-border bg-muted/40 px-3 py-2 text-sm">{notice}</p>
          )}
          {formError && (
            <p role="alert" className="rounded-xl border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {formError}
            </p>
          )}
          <Button type="submit" className="w-full" size="lg" disabled={isSubmitting || login.isPending}>
            {login.isPending ? 'Entrando…' : 'Entrar'}
          </Button>
        </form>
        <p className="mt-6 text-center text-sm text-muted-foreground">
          Não tem conta?{' '}
          <Link to="/register" className="font-semibold text-primary hover:underline">
            Cadastre-se
          </Link>
        </p>
      </div>
    </div>
  );
}
