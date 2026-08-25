import { ArrowRight, CheckCircle2, History } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ExerciseHistorySheet } from '@/components/ExerciseHistorySheet';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractApiError } from '@/lib/api/client';
import { useLogSet, useSession, useUpdateLog } from '@/lib/api/hooks';
import { formatWeight } from '@/lib/utils';
import { setTypeLabel } from '@/types/api';

export function SessionSetPage() {
  const { sessionId, exerciseId, setIndex } = useParams<{
    sessionId: string;
    exerciseId: string;
    setIndex: string;
  }>();
  const sid = Number(sessionId);
  const eid = Number(exerciseId);
  const index = Number(setIndex);
  const navigate = useNavigate();
  const { data, isPending, isError, error } = useSession(sid);
  const log = useLogSet(sid);
  const update = useUpdateLog();

  const exercise = data?.exercises.find((e) => e.exerciseId === eid);
  const set = exercise?.sets[index];
  const isLast = !!exercise && index === exercise.sets.length - 1;

  const plannedReps = set?.repsMin ?? 0;

  const [weight, setWeight] = useState('');
  const [reps, setReps] = useState('');
  const [historyOpen, setHistoryOpen] = useState(false);

  const prefillKey = useMemo(
    () =>
      set
        ? `${set.plannedSetId}-${set.currentLog?.id ?? 'new'}-${set.lastTime?.loggedAt ?? ''}`
        : '',
    [set],
  );

  useEffect(() => {
    if (!set) return;
    if (set.currentLog) {
      setWeight(formatWeight(set.currentLog.weightKg));
      setReps(String(set.currentLog.performedReps));
      return;
    }
    if (set.lastTime) {
      setWeight(formatWeight(set.lastTime.weightKg));
      setReps(String(set.lastTime.performedReps));
      return;
    }
    setWeight('');
    setReps(plannedReps > 0 ? String(plannedReps) : '');
  }, [prefillKey, plannedReps, set]);

  const saveAndContinue = async () => {
    if (!set) return;
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
      if (isLast) {
        navigate(`/sessions/${sid}`);
      } else {
        navigate(`/sessions/${sid}/exercises/${eid}/sets/${index + 1}`);
      }
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  const busy = log.isPending || update.isPending;
  const repsHint =
    set && set.repsMin !== set.repsMax ? `${set.repsMin}–${set.repsMax}` : null;

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-10 border-b border-border bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto flex h-14 max-w-3xl items-center gap-3 px-4">
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => navigate(`/sessions/${sid}`)}
            aria-label="Voltar"
          >
            <span className="text-xl">‹</span>
          </Button>
          <h1 className="flex-1 truncate text-base font-semibold">
            {exercise?.exerciseName ?? 'Série'}
          </h1>
          {exercise && (
            <Button
              type="button"
              variant="ghost"
              size="icon"
              onClick={() => setHistoryOpen(true)}
              aria-label="Histórico do exercício"
            >
              <History className="h-5 w-5" />
            </Button>
          )}
          {exercise && (
            <span className="text-xs text-muted-foreground">
              {index + 1}/{exercise.sets.length}
            </span>
          )}
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-4 py-8">
        {isPending && <p className="text-muted-foreground">Carregando…</p>}
        {isError && <p className="text-destructive">{extractApiError(error)}</p>}
        {data && !exercise && (
          <p className="text-muted-foreground">Exercício não encontrado nesta sessão.</p>
        )}
        {exercise && !set && (
          <p className="text-muted-foreground">Série não encontrada.</p>
        )}
        {set && (
          <div className="space-y-8">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Nome da série</p>
              <h2 className="mt-1 text-2xl font-bold tracking-tight">
                Série {index + 1} · {setTypeLabel[set.type]}
              </h2>
              {repsHint && (
                <p className="mt-1 text-sm text-muted-foreground">Alvo: {repsHint} reps</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="carga">Carga (kg)</Label>
              <Input
                id="carga"
                type="number"
                inputMode="decimal"
                step="0.5"
                autoFocus
                value={weight}
                onChange={(e) => setWeight(e.target.value)}
                className="h-14 text-center text-2xl font-semibold"
              />
              {set.lastTime && (
                <button
                  type="button"
                  onClick={() => setHistoryOpen(true)}
                  className="flex items-center gap-1.5 text-xs font-semibold text-primary"
                >
                  <History className="h-3.5 w-3.5" />
                  Última vez: {formatWeight(set.lastTime.weightKg)} kg · {set.lastTime.performedReps} reps
                </button>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="reps">Repetição</Label>
              <Input
                id="reps"
                type="number"
                inputMode="numeric"
                value={reps}
                onChange={(e) => setReps(e.target.value)}
                className="h-14 text-center text-2xl font-semibold"
              />
              {!set.currentLog && !set.lastTime && plannedReps > 0 && (
                <p className="text-xs text-muted-foreground">
                  Sugestão do treino:{' '}
                  {set.repsMin !== set.repsMax ? `${set.repsMin}–${set.repsMax}` : plannedReps}{' '}
                  reps
                </p>
              )}
            </div>

            <Button
              type="button"
              size="lg"
              className="w-full"
              onClick={saveAndContinue}
              disabled={busy}
            >
              {busy ? (
                'Salvando…'
              ) : isLast ? (
                <>
                  <CheckCircle2 className="h-5 w-5" />
                  Finalizar
                </>
              ) : (
                <>
                  Próximo
                  <ArrowRight className="h-5 w-5" />
                </>
              )}
            </Button>
          </div>
        )}
      </main>

      {set && (
        <ExerciseHistorySheet
          open={historyOpen}
          onClose={() => setHistoryOpen(false)}
          sessionId={sid}
          exerciseId={eid}
          currentOrderIndex={set.orderIndex}
          onUse={(past) => {
            setWeight(formatWeight(past.weightKg));
            setReps(String(past.performedReps));
            setHistoryOpen(false);
          }}
        />
      )}
    </div>
  );
}
