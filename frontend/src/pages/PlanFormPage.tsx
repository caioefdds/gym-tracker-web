import { zodResolver } from '@hookform/resolvers/zod';
import { Trash2 } from 'lucide-react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, useParams } from 'react-router-dom';
import { z } from 'zod';
import { Layout } from '@/components/Layout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractApiError } from '@/lib/api/client';
import {
  useCreatePlan,
  useDeletePlan,
  usePlanDetail,
  useUpdatePlan,
} from '@/lib/api/hooks';

const schema = z.object({
  name: z.string().min(1, 'Obrigatório').max(120),
  startDate: z.string().min(1, 'Obrigatório'),
  isActive: z.boolean(),
});

type FormData = z.infer<typeof schema>;

export function PlanFormPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEditing = id !== 'new' && id != null;
  const planId = isEditing ? Number(id) : null;

  const { data: existing } = usePlanDetail(planId);
  const create = useCreatePlan();
  const update = useUpdatePlan();
  const remove = useDeletePlan();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      startDate: new Date().toISOString().slice(0, 10),
      isActive: true,
    },
  });

  useEffect(() => {
    if (existing && isEditing) {
      reset({
        name: existing.name,
        startDate: existing.startDate,
        isActive: existing.isActive,
      });
    }
  }, [existing, isEditing, reset]);

  const onSubmit = handleSubmit(async (data) => {
    try {
      if (isEditing && planId) {
        await update.mutateAsync({ id: planId, ...data });
        navigate(`/plans/${planId}`);
      } else {
        const created = await create.mutateAsync(data);
        navigate(`/plans/${created.id}`);
      }
    } catch (e) {
      alert(extractApiError(e));
    }
  });

  const onDelete = async () => {
    if (!planId) return;
    if (!confirm('Excluir esta ficha? Treinos, exercícios e histórico serão apagados.')) return;
    try {
      await remove.mutateAsync(planId);
      navigate('/');
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <Layout
      title={isEditing ? 'Editar Ficha' : 'Nova Ficha'}
      back={isEditing ? `/plans/${planId}` : '/'}
      actions={
        isEditing ? (
          <Button variant="ghost" size="icon" onClick={onDelete} aria-label="Excluir">
            <Trash2 className="h-4 w-4" />
          </Button>
        ) : null
      }
    >
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="space-y-1.5">
          <Label htmlFor="name">Nome da ficha</Label>
          <Input id="name" placeholder="Ex.: Ficha do Caio 22/02/2026" {...register('name')} />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="startDate">Data de início</Label>
          <Input id="startDate" type="date" {...register('startDate')} />
          {errors.startDate && (
            <p className="text-xs text-destructive">{errors.startDate.message}</p>
          )}
        </div>
        <div className="flex items-center gap-3 rounded-xl border border-border p-4">
          <input id="isActive" type="checkbox" className="h-4 w-4 accent-primary" {...register('isActive')} />
          <Label htmlFor="isActive" className="cursor-pointer text-foreground">
            Ficha ativa
          </Label>
        </div>
        <Button
          type="submit"
          size="lg"
          className="w-full"
          disabled={isSubmitting || create.isPending || update.isPending}
        >
          Salvar
        </Button>
      </form>
    </Layout>
  );
}
