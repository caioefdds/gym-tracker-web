import { zodResolver } from '@hookform/resolvers/zod';
import { Plus, Trash2, X } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, useParams } from 'react-router-dom';
import { z } from 'zod';
import { Layout } from '@/components/Layout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select } from '@/components/ui/select';
import { extractApiError } from '@/lib/api/client';
import {
  useCreateExercise,
  useCreatePlannedSet,
  useDeleteExercise,
  useDeletePlannedSet,
  usePlanDetail,
  useUpdateExercise,
  useUpdatePlannedSet,
} from '@/lib/api/hooks';
import {
  setTypeShortLabel,
  type PlannedSetNode,
  type SetType,
} from '@/types/api';

const schema = z.object({
  name: z.string().min(1, 'Obrigatório').max(120),
});

type FormData = z.infer<typeof schema>;

const DEFAULTS: Record<SetType, { repsMin: number; repsMax: number }> = {
  WARMUP: { repsMin: 15, repsMax: 20 },
  WORKING: { repsMin: 8, repsMax: 12 },
  STRENGTH: { repsMin: 4, repsMax: 6 },
};

export function ExerciseFormPage() {
  const { planId, workoutId, exerciseId } = useParams<{
    planId: string;
    workoutId: string;
    exerciseId: string;
  }>();
  const planIdNum = Number(planId);
  const workoutIdNum = Number(workoutId);
  const isEditing = exerciseId !== 'new' && exerciseId != null;
  const initialExerciseId = isEditing ? Number(exerciseId) : null;
  const [resolvedId, setResolvedId] = useState<number | null>(initialExerciseId);

  const navigate = useNavigate();
  const { data: plan } = usePlanDetail(planIdNum);
  const create = useCreateExercise(planIdNum, workoutIdNum);
  const update = useUpdateExercise(planIdNum);
  const remove = useDeleteExercise(planIdNum);

  const existing = useMemo(() => {
    if (!plan || !resolvedId) return null;
    for (const w of plan.workouts) {
      const ex = w.exercises.find((e) => e.id === resolvedId);
      if (ex) return ex;
    }
    return null;
  }, [plan, resolvedId]);

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

  const saveName = handleSubmit(async (data) => {
    try {
      if (resolvedId && existing) {
        await update.mutateAsync({
          id: resolvedId,
          name: data.name,
          orderIndex: existing.orderIndex,
        });
      } else {
        const created = await create.mutateAsync({ name: data.name });
        setResolvedId(created.id);
      }
    } catch (e) {
      alert(extractApiError(e));
    }
  });

  const onDelete = async () => {
    if (!resolvedId) return;
    if (!confirm('Excluir este exercício? Histórico será apagado.')) return;
    try {
      await remove.mutateAsync(resolvedId);
      navigate(`/plans/${planIdNum}`);
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <Layout
      title={isEditing ? 'Editar Exercício' : 'Novo Exercício'}
      back={`/plans/${planIdNum}`}
      actions={
        resolvedId ? (
          <Button variant="ghost" size="icon" onClick={onDelete} aria-label="Excluir">
            <Trash2 className="h-4 w-4" />
          </Button>
        ) : null
      }
    >
      <form onSubmit={saveName} className="space-y-4">
        <div className="space-y-1.5">
          <Label htmlFor="name">Nome do exercício</Label>
          <Input
            id="name"
            placeholder="Ex.: Supino Reto na Polia"
            autoFocus={!resolvedId}
            {...register('name')}
          />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>
        <Button
          type="submit"
          size="md"
          disabled={isSubmitting || create.isPending || update.isPending}
        >
          Salvar nome
        </Button>
      </form>

      {resolvedId && (
        <div className="mt-10">
          <h3 className="mb-3 text-base font-semibold">Séries programadas</h3>
          <PlannedSetsEditor
            planId={planIdNum}
            exerciseId={resolvedId}
            sets={existing?.plannedSets ?? []}
          />
        </div>
      )}
    </Layout>
  );
}

function PlannedSetsEditor({
  planId,
  exerciseId,
  sets,
}: {
  planId: number;
  exerciseId: number;
  sets: PlannedSetNode[];
}) {
  const create = useCreatePlannedSet(planId, exerciseId);

  const onAdd = async (type: SetType) => {
    const def = DEFAULTS[type];
    try {
      await create.mutateAsync({
        type,
        repsMin: def.repsMin,
        repsMax: def.repsMax,
      });
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <div className="space-y-3">
      {sets.length === 0 && (
        <p className="text-sm text-muted-foreground">Nenhuma série cadastrada.</p>
      )}
      {sets.map((s, i) => (
        <PlannedSetTile key={s.id} planId={planId} index={i + 1} row={s} />
      ))}
      <div className="flex flex-wrap gap-2 pt-2">
        {(['WARMUP', 'WORKING', 'STRENGTH'] as SetType[]).map((t) => (
          <Button key={t} variant="outline" size="sm" onClick={() => onAdd(t)}>
            <Plus className="h-3.5 w-3.5" />
            {setTypeShortLabel[t]}
          </Button>
        ))}
      </div>
    </div>
  );
}

function PlannedSetTile({
  planId,
  index,
  row,
}: {
  planId: number;
  index: number;
  row: PlannedSetNode;
}) {
  const update = useUpdatePlannedSet(planId);
  const remove = useDeletePlannedSet(planId);
  const [type, setType] = useState<SetType>(row.type);
  const [repsMin, setRepsMin] = useState(row.repsMin);
  const [repsMax, setRepsMax] = useState(row.repsMax);

  useEffect(() => {
    setType(row.type);
    setRepsMin(row.repsMin);
    setRepsMax(row.repsMax);
  }, [row.id, row.type, row.repsMin, row.repsMax]);

  const save = async (override?: Partial<{ type: SetType; repsMin: number; repsMax: number }>) => {
    const next = {
      type: override?.type ?? type,
      repsMin: override?.repsMin ?? repsMin,
      repsMax: override?.repsMax ?? repsMax,
    };
    if (next.repsMax < next.repsMin) next.repsMax = next.repsMin;
    try {
      await update.mutateAsync({
        id: row.id,
        type: next.type,
        repsMin: next.repsMin,
        repsMax: next.repsMax,
        orderIndex: row.orderIndex,
      });
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <div className="flex items-center gap-2 rounded-xl bg-muted/60 p-3">
      <span className="flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
        {index}
      </span>
      <Select
        value={type}
        onChange={(e) => {
          const nv = e.target.value as SetType;
          setType(nv);
          save({ type: nv });
        }}
        className="h-9 w-28 px-2 text-xs"
      >
        <option value="WARMUP">Aquec.</option>
        <option value="WORKING">Trab.</option>
        <option value="STRENGTH">Força</option>
      </Select>
      <Input
        type="number"
        min={1}
        value={repsMin}
        onChange={(e) => setRepsMin(Number(e.target.value))}
        onBlur={() => save()}
        className="h-9 w-16 text-center text-sm"
        aria-label="Repetições mínimas"
      />
      <span className="text-muted-foreground">–</span>
      <Input
        type="number"
        min={1}
        value={repsMax}
        onChange={(e) => setRepsMax(Number(e.target.value))}
        onBlur={() => save()}
        className="h-9 w-16 text-center text-sm"
        aria-label="Repetições máximas"
      />
      <Button
        variant="ghost"
        size="icon"
        onClick={() => remove.mutate(row.id)}
        aria-label="Remover série"
      >
        <X className="h-4 w-4" />
      </Button>
    </div>
  );
}
