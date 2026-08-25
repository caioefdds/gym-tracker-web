import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { ChevronRight, History, Play, PlayCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { extractApiError } from '@/lib/api/client';
import { usePlanSessions, useStartSession } from '@/lib/api/hooks';
import type { PlanDetail } from '@/types/api';

interface Props {
  planId: number;
  plan: PlanDetail;
}

export function StartTab({ planId, plan }: Props) {
  const navigate = useNavigate();
  const start = useStartSession();

  const onStart = async (workoutId: number) => {
    try {
      const { sessionId } = await start.mutateAsync(workoutId);
      navigate(`/sessions/${sessionId}`);
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
    <div className="space-y-8">
      {plan.workouts.length === 0 ? (
        <EmptyState
          icon={PlayCircle}
          message="Cadastre pelo menos um treino para começar uma sessão."
        />
      ) : (
        <div className="space-y-3">
          <p className="mb-2 px-1 text-sm font-medium text-muted-foreground">
            Escolha o treino para iniciar
          </p>
          {plan.workouts.map((w) => (
            <Card
              key={w.id}
              onClick={() => onStart(w.id)}
              className="cursor-pointer bg-accent transition-opacity hover:opacity-90"
            >
              <div className="flex items-center gap-4 p-5">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary text-primary-foreground">
                  <Play className="h-5 w-5" />
                </div>
                <div className="flex-1">
                  <h3 className="font-bold text-accent-foreground">{w.name}</h3>
                  <p className="text-xs text-accent-foreground/80">
                    {w.exercises.length} exercícios
                  </p>
                </div>
                <ChevronRight className="h-5 w-5 text-accent-foreground" />
              </div>
            </Card>
          ))}
        </div>
      )}

      <PlanSessionHistory planId={planId} />
    </div>
  );
}

function PlanSessionHistory({ planId }: { planId: number }) {
  const navigate = useNavigate();
  const { data, isPending, isError, error } = usePlanSessions(planId);

  return (
    <section className="space-y-3">
      <div className="flex items-center gap-2 px-1">
        <History className="h-4 w-4 text-muted-foreground" />
        <h2 className="text-sm font-medium text-muted-foreground">Histórico de treinos</h2>
      </div>

      {isPending && <p className="text-sm text-muted-foreground">Carregando histórico…</p>}
      {isError && <p className="text-sm text-destructive">{extractApiError(error)}</p>}
      {data && data.length === 0 && (
        <p className="px-1 text-sm text-muted-foreground">
          Ainda não há treinos registrados nesta ficha.
        </p>
      )}
      {data &&
        data.map((session) => {
          const date = session.finishedAt ?? session.startedAt;
          return (
            <Card
              key={session.sessionId}
              onClick={() => navigate(`/sessions/${session.sessionId}/review`)}
              className="cursor-pointer transition-opacity hover:opacity-90"
            >
              <div className="flex items-center gap-3 p-4">
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <h3 className="truncate font-semibold">{session.workoutName}</h3>
                    {session.finishedAt == null && <Badge>Em andamento</Badge>}
                  </div>
                  <p className="mt-0.5 text-xs capitalize text-muted-foreground">
                    {format(new Date(date), 'EEE, dd/MM/yyyy', { locale: ptBR })}
                    {' · '}
                    {session.loggedSets} {session.loggedSets === 1 ? 'série' : 'séries'}
                  </p>
                </div>
                <ChevronRight className="h-5 w-5 shrink-0 text-muted-foreground" />
              </div>
            </Card>
          );
        })}
    </section>
  );
}
