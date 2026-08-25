import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Pencil, Play, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Layout } from '@/components/Layout';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { extractApiError } from '@/lib/api/client';
import {
  useDeleteLog,
  useDeleteSession,
  useLogSet,
  useSession,
  useUpdateLog,
} from '@/lib/api/hooks';
import { formatWeight } from '@/lib/utils';
import { setTypeShortLabel, type SessionPlannedSet } from '@/types/api';

export function SessionReviewPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);
  const navigate = useNavigate();
  const { data, isPending, isError, error } = useSession(sid);
  const removeSession = useDeleteSession();

  const backTo = data ? `/plans/${data.planId}?tab=start` : '/';
  const date = data ? data.finishedAt ?? data.startedAt : null;
  const unfinished = !!data && data.finishedAt == null;

  const onDeleteSession = async () => {
    if (!confirm('Apagar este treino do histórico? Essa ação não pode ser desfeita.')) return;
    try {
      await removeSession.mutateAsync(sid);
      navigate(backTo, { replace: true });
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <Layout
      title={data?.workoutName ?? 'Treino'}
      back={backTo}
      actions={
        <Button
          type="button"
          variant="ghost"
          size="icon"
          onClick={onDeleteSession}
          disabled={removeSession.isPending}
          aria-label="Apagar treino"
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      }
    >
      {isPending && <p className="text-muted-foreground">Carregando…</p>}
      {isError && <p className="text-destructive">{extractApiError(error)}</p>}
      {data && (
        <div className="space-y-4">
          <div>
            {date && (
              <p className="text-sm capitalize text-muted-foreground">
                {format(new Date(date), "EEEE, dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
              </p>
            )}
            {unfinished && (
              <p className="mt-1 text-xs font-semibold text-primary">Não finalizado</p>
            )}
          </div>

          {unfinished && (
            <Button
              type="button"
              variant="outline"
              className="w-full"
              onClick={() => navigate(`/sessions/${sid}`)}
            >
              <Play className="h-4 w-4" />
              Continuar treino
            </Button>
          )}

          {data.exercises.length === 0 && (
            <p className="text-sm text-muted-foreground">Nenhum exercício neste treino.</p>
          )}

          {data.exercises.map((ex) => (
            <Card key={ex.exerciseId}>
              <CardHeader>
                <CardTitle className="text-base">{ex.exerciseName}</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {ex.sets.length === 0 && (
                  <p className="text-sm text-muted-foreground">Sem séries.</p>
                )}
                {ex.sets.map((set, i) => (
                  <SetRow
                    key={set.plannedSetId}
                    sessionId={sid}
                    index={i}
                    set={set}
                  />
                ))}
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </Layout>
  );
}

function SetRow({
  sessionId,
  index,
  set,
}: {
  sessionId: number;
  index: number;
  set: SessionPlannedSet;
}) {
  const [editing, setEditing] = useState(false);
  const [weight, setWeight] = useState('');
  const [reps, setReps] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const log = useLogSet(sessionId);
  const update = useUpdateLog(sessionId);
  const remove = useDeleteLog();

  const startEdit = () => {
    setFormError(null);
    setWeight(set.currentLog ? formatWeight(set.currentLog.weightKg) : '');
    setReps(set.currentLog ? String(set.currentLog.performedReps) : '');
    setEditing(true);
  };

  const save = async () => {
    const w = parseFloat(weight.replace(',', '.'));
    const r = parseInt(reps, 10);
    if (Number.isNaN(w) || w <= 0 || Number.isNaN(r) || r <= 0) {
      setFormError('Informe carga e repetições válidas.');
      return;
    }
    try {
      if (set.currentLog) {
        await update.mutateAsync({
          id: set.currentLog.id,
          weightKg: w,
          performedReps: r,
        });
      } else {
        await log.mutateAsync({
          plannedSetId: set.plannedSetId,
          weightKg: w,
          performedReps: r,
        });
      }
      setEditing(false);
      setFormError(null);
    } catch (e) {
      setFormError(extractApiError(e));
    }
  };

  const onDelete = async () => {
    if (!set.currentLog) return;
    if (!confirm('Apagar esta série?')) return;
    try {
      await remove.mutateAsync(set.currentLog.id);
    } catch (e) {
      setFormError(extractApiError(e));
    }
  };

  const busy = log.isPending || update.isPending || remove.isPending;

  if (editing) {
    return (
      <div className="space-y-2 rounded-xl bg-muted/60 p-3">
        <p className="text-xs text-muted-foreground">
          Série {index + 1} · {setTypeShortLabel[set.type]}
        </p>
        <div className="flex gap-2">
          <Input
            type="number"
            inputMode="decimal"
            step="0.5"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            placeholder="kg"
            className="h-10"
            autoFocus
          />
          <Input
            type="number"
            inputMode="numeric"
            value={reps}
            onChange={(e) => setReps(e.target.value)}
            placeholder="reps"
            className="h-10"
          />
        </div>
        {formError && <p className="text-xs text-destructive">{formError}</p>}
        <div className="flex gap-2">
          <Button type="button" size="sm" onClick={save} disabled={busy}>
            Salvar
          </Button>
          <Button type="button" size="sm" variant="ghost" onClick={() => setEditing(false)}>
            Cancelar
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2 rounded-xl bg-muted/60 px-3 py-2">
      <div className="min-w-0 flex-1">
        <p className="text-xs text-muted-foreground">
          Série {index + 1} · {setTypeShortLabel[set.type]}
        </p>
        {set.currentLog ? (
          <p className="text-sm font-semibold">
            {formatWeight(set.currentLog.weightKg)} kg × {set.currentLog.performedReps} reps
          </p>
        ) : (
          <p className="text-sm text-muted-foreground">Não registrada</p>
        )}
        {formError && <p className="text-xs text-destructive">{formError}</p>}
      </div>
      <Button type="button" variant="ghost" size="icon" onClick={startEdit} aria-label="Editar série">
        <Pencil className="h-4 w-4" />
      </Button>
      {set.currentLog && (
        <Button
          type="button"
          variant="ghost"
          size="icon"
          onClick={onDelete}
          disabled={busy}
          aria-label="Apagar série"
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}
