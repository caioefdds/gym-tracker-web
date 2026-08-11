import { ChevronRight, ListTodo, Pencil, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import type { PlanDetail } from '@/types/api';

interface Props {
  planId: number;
  plan: PlanDetail;
}

export function WorkoutsTab({ planId, plan }: Props) {
  const navigate = useNavigate();

  if (plan.workouts.length === 0) {
    return (
      <EmptyState
        icon={ListTodo}
        message="Nenhum treino cadastrado. Adicione seu primeiro treino (A, B, C…)."
        actionLabel="Novo treino"
        onAction={() => navigate(`/plans/${planId}/workouts/new`)}
      />
    );
  }

  return (
    <div className="space-y-4">
      {plan.workouts.map((w) => (
        <Card key={w.id} className="overflow-hidden">
          <div className="flex items-center justify-between p-5 pb-3">
            <h3 className="text-base font-semibold">{w.name}</h3>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => navigate(`/plans/${planId}/workouts/${w.id}/edit`)}
              aria-label="Editar treino"
            >
              <Pencil className="h-4 w-4" />
            </Button>
          </div>
          {w.exercises.length === 0 ? (
            <p className="px-5 pb-3 text-sm text-muted-foreground">
              Nenhum exercício cadastrado.
            </p>
          ) : (
            <ul className="divide-y divide-border">
              {w.exercises.map((e, i) => (
                <li key={e.id}>
                  <Link
                    to={`/plans/${planId}/workouts/${w.id}/exercises/${e.id}`}
                    className="flex items-center gap-3 px-5 py-3 hover:bg-muted/40"
                  >
                    <span className="flex h-7 w-7 items-center justify-center rounded-full bg-secondary text-xs font-bold text-secondary-foreground">
                      {i + 1}
                    </span>
                    <span className="flex-1 text-sm">{e.name}</span>
                    <span className="text-xs text-muted-foreground">
                      {e.plannedSets.length} {e.plannedSets.length === 1 ? 'série' : 'séries'}
                    </span>
                    <ChevronRight className="h-4 w-4 text-muted-foreground" />
                  </Link>
                </li>
              ))}
            </ul>
          )}
          <div className="border-t border-border p-3">
            <Button
              variant="ghost"
              size="sm"
              onClick={() =>
                navigate(`/plans/${planId}/workouts/${w.id}/exercises/new`)
              }
            >
              <Plus className="h-4 w-4" />
              Novo exercício
            </Button>
          </div>
        </Card>
      ))}
      <Button
        variant="outline"
        className="w-full"
        size="lg"
        onClick={() => navigate(`/plans/${planId}/workouts/new`)}
      >
        <Plus className="h-4 w-4" />
        Novo treino
      </Button>
    </div>
  );
}
