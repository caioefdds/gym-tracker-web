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
  useCreateWorkout,
  useDeleteWorkout,
  usePlanDetail,
  useUpdateWorkout,
} from '@/lib/api/hooks';

const schema = z.object({
  name: z.string().min(1, 'Obrigatório').max(120),
});

type FormData = z.infer<typeof schema>;

export function WorkoutFormPage() {
  const { planId, workoutId } = useParams<{ planId: string; workoutId: string }>();
  const navigate = useNavigate();
  const planIdNum = Number(planId);
  const isEditing = workoutId !== 'new' && workoutId != null;
  const workoutIdNum = isEditing ? Number(workoutId) : null;

  const { data: plan } = usePlanDetail(planIdNum);
  const create = useCreateWorkout(planIdNum);
  const update = useUpdateWorkout(planIdNum);
  const remove = useDeleteWorkout(planIdNum);

  const existing = plan?.workouts.find((w) => w.id === workoutIdNum);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { name: '' },
  });

  useEffect(() => {
    if (existing) reset({ name: existing.name });
  }, [existing, reset]);

  const onSubmit = handleSubmit(async (data) => {
    try {
      if (isEditing && workoutIdNum && existing) {
        await update.mutateAsync({
          id: workoutIdNum,
          name: data.name,
          orderIndex: existing.orderIndex,
        });
      } else {
        await create.mutateAsync({ name: data.name });
      }
      navigate(`/plans/${planIdNum}`);
    } catch (e) {
      alert(extractApiError(e));
    }
  });

  const onDelete = async () => {
    if (!workoutIdNum) return;
    if (!confirm('Excluir este treino? Exercícios e histórico serão apagados.')) return;
    try {
      await remove.mutateAsync(workoutIdNum);
      navigate(`/plans/${planIdNum}`);
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <Layout
      title={isEditing ? 'Editar Treino' : 'Novo Treino'}
      back={`/plans/${planIdNum}`}
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
          <Label htmlFor="name">Nome do treino</Label>
          <Input
            id="name"
            placeholder="Ex.: Treino A - Peitoral"
            autoFocus={!isEditing}
            {...register('name')}
          />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
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
