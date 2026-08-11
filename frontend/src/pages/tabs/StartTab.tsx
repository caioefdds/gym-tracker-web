import { ChevronRight, Play, PlayCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { Card } from '@/components/ui/card';
import { extractApiError } from '@/lib/api/client';
import { useStartSession } from '@/lib/api/hooks';
import type { PlanDetail } from '@/types/api';

interface Props {
  planId: number;
  plan: PlanDetail;
}

export function StartTab({ plan }: Props) {
  const navigate = useNavigate();
  const start = useStartSession();

  if (plan.workouts.length === 0) {
    return (
      <EmptyState
        icon={PlayCircle}
        message="Cadastre pelo menos um treino para começar uma sessão."
      />
    );
  }

  const onStart = async (workoutId: number) => {
    try {
      const { sessionId } = await start.mutateAsync(workoutId);
      navigate(`/sessions/${sessionId}`);
    } catch (e) {
      alert(extractApiError(e));
    }
  };

  return (
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
  );
}
