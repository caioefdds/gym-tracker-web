import { CheckCircle2, ChevronRight, X } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { extractApiError } from '@/lib/api/client';
import { useFinishSession, useSession } from '@/lib/api/hooks';
import { cn } from '@/lib/utils';
import type { SessionExerciseNode } from '@/types/api';

function isExerciseDone(ex: SessionExerciseNode): boolean {
  return ex.sets.length > 0 && ex.sets.every((s) => s.currentLog != null);
}

function firstOpenSetIndex(ex: SessionExerciseNode): number {
  const idx = ex.sets.findIndex((s) => s.currentLog == null);
  return idx === -1 ? 0 : idx;
}

export function ActiveSessionPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const sid = Number(sessionId);
  const navigate = useNavigate();
  const { data, isPending, isError, error } = useSession(sid);
  const finish = useFinishSession();

  const onFinishWorkout = async () => {
    if (!confirm('Finalizar este treino?')) return;
    try {
      await finish.mutateAsync(sid);
      if (data) navigate(`/plans/${data.planId}`);
      else navigate('/');
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  const openExercise = (ex: SessionExerciseNode) => {
    if (ex.sets.length === 0) {
      alert('Este exercício não tem séries cadastradas.');
      return;
    }
    navigate(
      `/sessions/${sid}/exercises/${ex.exerciseId}/sets/${firstOpenSetIndex(ex)}`,
    );
  };

  return (
    <div className="min-h-screen bg-background pb-32">
      <header className="sticky top-0 z-10 border-b border-border bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="mx-auto flex h-14 max-w-3xl items-center gap-3 px-4">
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => {
              if (data) navigate(`/plans/${data.planId}`);
              else navigate(-1);
            }}
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
          <div className="space-y-3">
            <p className="px-1 text-sm font-medium text-muted-foreground">
              Escolha o exercício
            </p>
            {data.exercises.map((ex) => {
              const done = isExerciseDone(ex);
              const logged = ex.sets.filter((s) => s.currentLog).length;
              return (
                <Card
                  key={ex.exerciseId}
                  onClick={() => openExercise(ex)}
                  className={cn(
                    'cursor-pointer transition-opacity hover:opacity-90',
                    done && 'border-primary/40 bg-accent/40',
                  )}
                >
                  <div className="flex items-center gap-4 p-5">
                    <div className="flex-1">
                      <h3 className="font-bold">{ex.exerciseName}</h3>
                      {done ? (
                        <p className="mt-1 flex items-center gap-1.5 text-xs font-semibold text-primary">
                          <CheckCircle2 className="h-3.5 w-3.5" />
                          Concluído
                        </p>
                      ) : (
                        <p className="mt-1 text-xs text-muted-foreground">
                          {ex.sets.length === 0
                            ? 'Sem séries'
                            : `${logged}/${ex.sets.length} séries`}
                        </p>
                      )}
                    </div>
                    <ChevronRight className="h-5 w-5 text-muted-foreground" />
                  </div>
                </Card>
              );
            })}
          </div>
        )}
      </main>

      <div className="fixed bottom-0 left-0 right-0 border-t border-border bg-background/95 backdrop-blur">
        <div className="mx-auto max-w-3xl p-4">
          <Button
            type="button"
            onClick={onFinishWorkout}
            size="lg"
            className="w-full"
          >
            <CheckCircle2 className="h-5 w-5" />
            Finalizar treino
          </Button>
        </div>
      </div>
    </div>
  );
}
