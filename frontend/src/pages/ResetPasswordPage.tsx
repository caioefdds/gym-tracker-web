import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractApiError } from '@/lib/api/client';
import { useResetPassword } from '@/lib/api/hooks';

const schema = z
  .object({
    password: z.string().min(8, 'Mínimo 8 caracteres'),
    confirm: z.string().min(8, 'Mínimo 8 caracteres'),
  })
  .refine((data) => data.password === data.confirm, {
    message: 'As senhas não coincidem',
    path: ['confirm'],
  });

type FormData = z.infer<typeof schema>;

export function ResetPasswordPage() {
  const [params] = useSearchParams();
  const token = params.get('token') ?? '';
  const navigate = useNavigate();
  const reset = useResetPassword();
  const [formError, setFormError] = useState<string | null>(
    token ? null : 'Link inválido. Peça um novo e-mail de redefinição.',
  );
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (data) => {
    if (!token) return;
    setFormError(null);
    try {
      await reset.mutateAsync({ token, password: data.password });
      navigate('/login', { replace: true, state: { notice: 'Senha redefinida. Entre com a nova senha.' } });
    } catch (e) {
      setFormError(extractApiError(e));
    }
  });

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold tracking-tight">Gym Tracker</h1>
          <p className="mt-2 text-sm text-muted-foreground">Nova senha</p>
        </div>
        <form onSubmit={onSubmit} className="space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="password">Nova senha</Label>
            <Input
              id="password"
              type="password"
              autoComplete="new-password"
              disabled={!token}
              {...register('password')}
            />
            {errors.password && (
              <p className="text-xs text-destructive">{errors.password.message}</p>
            )}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="confirm">Confirmar senha</Label>
            <Input
              id="confirm"
              type="password"
              autoComplete="new-password"
              disabled={!token}
              {...register('confirm')}
            />
            {errors.confirm && (
              <p className="text-xs text-destructive">{errors.confirm.message}</p>
            )}
          </div>
          {formError && (
            <p role="alert" className="rounded-xl border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {formError}
            </p>
          )}
          <Button type="submit" className="w-full" size="lg" disabled={!token || isSubmitting || reset.isPending}>
            {reset.isPending ? 'Salvando…' : 'Salvar senha'}
          </Button>
        </form>
        <p className="mt-6 text-center text-sm text-muted-foreground">
          <Link to="/forgot-password" className="font-semibold text-primary hover:underline">
            Pedir um novo link
          </Link>
        </p>
      </div>
    </div>
  );
}
