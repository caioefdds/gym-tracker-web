import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Check, CheckCircle2, History, RotateCcw, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractApiError } from '@/lib/api/client';
import {
  useFinishSession,
  useLogSet,
  useSession,
  useUpdateLog,
} from '@/lib/api/hooks';
import { cn, formatWeight } from '@/lib/utils';
import {
  setTypeLabel,
  type SessionPlannedSet,
} from '@/types/api';

export function ActiveSessionPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);
  const navigate = useNavigate();
  const { data, isPending, isError, error } = useSession(sid);
  const finish = useFinishSession();

  const onFinish = async () => {
    if (!confirm('Finalizar este treino?')) return;
    try {
      await finish.mutateAsync(sid);
      if (data) navigate(`/plans/${data.planId}`);
      else navigate('/');
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <div className="min-h-screen bg-background pb-32">
      <header className="sticky top-0 z-10 border-b border-border bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto flex h-14 max-w-3xl items-center gap-3 px-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate(-1)}
            aria-label="Sair"
          >
            <X className="h-4 w-4" />
          </Button>
          <h1 className="flex-1 truncate text-base font-semibold">
            {data?.workoutName ?? 'Sessão'}
          </h1>
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-4 py-6">
        {isPending && <p className="text-muted-foreground">Carregando…</p>}
        {isError && <p className="text-destructive">{extractApiError(error)}</p>}
        {data && data.exercises.length === 0 && (
          <p className="text-muted-foreground">
            Este treino não tem exercícios cadastrados.
          </p>
        )}
        {data && data.exercises.length > 0 && (
          <div className="space-y-4">
            {data.exercises.map((ex, i) => (
              <Card key={ex.exerciseId}>
                <CardHeader>
                  <div className="flex items-center gap-3">
                    <span className="flex h-7 w-7 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                      {i + 1}
                    </span>
                    <CardTitle>{ex.exerciseName}</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  {ex.sets.length === 0 ? (
                    <p className="text-sm text-muted-foreground">
                      Sem séries cadastradas.
                    </p>
                  ) : (
                    <div className="space-y-3">
                      {ex.sets.map((s, j) => (
                        <SetTile
                          key={s.plannedSetId}
                          sessionId={sid}
                          set={s}
                          index={j + 1}
                        />
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </main>

      <div className="fixed bottom-0 left-0 right-0 border-t border-border bg-background/95 backdrop-blur">
        <div className="mx-auto max-w-3xl p-4">
          <Button onClick={onFinish} size="lg" className="w-full">
            <CheckCircle2 className="h-5 w-5" />
            Finalizar treino
          </Button>
        </div>
      </div>
    </div>
  );
}

function SetTile({
  sessionId,
  set,
  index,
}: {
  sessionId: number;
  set: SessionPlannedSet;
  index: number;
}) {
  const log = useLogSet(sessionId);
  const update = useUpdateLog(sessionId);

  const [weight, setWeight] = useState<string>(
    set.currentLog ? formatWeight(set.currentLog.weightKg) : '',
  );
  const [reps, setReps] = useState<string>(
    set.currentLog ? String(set.currentLog.performedReps) : '',
  );

  useEffect(() => {
    if (set.currentLog) {
      setWeight(formatWeight(set.currentLog.weightKg));
      setReps(String(set.currentLog.performedReps));
    }
  }, [set.currentLog?.id, set.currentLog?.weightKg, set.currentLog?.performedReps]);

  const completed = !!set.currentLog;
  const repsRange =
    set.repsMin === set.repsMax
      ? `${set.repsMin} reps`
      : `${set.repsMin}-${set.repsMax} reps`;

  const save = async () => {
    const w = parseFloat(weight.replace(',', '.'));
    const r = parseInt(reps, 10);
    if (Number.isNaN(w) || w <= 0 || Number.isNaN(r) || r <= 0) {
      alert('Informe carga e repetições válidas.');
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
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <div
      className={cn(
        'rounded-xl border p-4 transition-colors',
        completed
          ? 'border-primary/30 bg-accent/40'
          : 'border-border bg-muted/40',
      )}
    >
      <div className="mb-3 flex items-center gap-2">
        <span className="rounded-md bg-secondary px-2 py-0.5 text-xs font-bold text-secondary-foreground">
          Série {index} · {setTypeLabel[set.type]}
        </span>
        <span className="text-xs text-muted-foreground">{repsRange}</span>
        {completed && (
          <CheckCircle2 className="ml-auto h-4 w-4 text-primary" aria-hidden />
        )}
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div className="space-y-1">
          <Label htmlFor={`w-${set.plannedSetId}`} className="text-xs">
            Carga (kg)
          </Label>
          <Input
            id={`w-${set.plannedSetId}`}
            type="number"
            inputMode="decimal"
            step="0.5"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            className="h-10 text-center"
          />
        </div>
        <div className="space-y-1">
          <Label htmlFor={`r-${set.plannedSetId}`} className="text-xs">
            Reps feitas
          </Label>
          <Input
            id={`r-${set.plannedSetId}`}
            type="number"
            inputMode="numeric"
            value={reps}
            onChange={(e) => setReps(e.target.value)}
            className="h-10 text-center"
          />
        </div>
      </div>
      <LastTimeLine last={set.lastTime} />
      <div className="mt-3 flex justify-end">
        <Button
          size="sm"
          variant={completed ? 'outline' : 'primary'}
          onClick={save}
          disabled={log.isPending || update.isPending}
        >
          {completed ? (
            <>
              <RotateCcw className="h-4 w-4" />
              Atualizar
            </>
          ) : (
            <>
              <Check className="h-4 w-4" />
              Concluir série
            </>
          )}
        </Button>
      </div>
    </div>
  );
}

function LastTimeLine({ last }: { last: SessionPlannedSet['lastTime'] }) {
  if (!last) {
    return (
      <p className="mt-2 text-xs italic text-muted-foreground">
        Sem registro anterior
      </p>
    );
  }
  const date = format(new Date(last.loggedAt), 'dd/MM', { locale: ptBR });
  return (
    <div className="mt-2 flex items-center gap-1.5">
      <History className="h-3.5 w-3.5 text-primary" />
      <span className="text-xs font-semibold text-primary">
        Última vez: {formatWeight(last.weightKg)}kg × {last.performedReps} ({date})
      </span>
    </div>
  );
}
