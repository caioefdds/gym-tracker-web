import { TrendingUp } from 'lucide-react';
import { useState } from 'react';
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { EmptyState } from '@/components/EmptyState';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { extractApiError } from '@/lib/api/client';
import { useExerciseProgress, useExercisesWithLogs } from '@/lib/api/hooks';
import { cn } from '@/lib/utils';
import type { ExerciseSummary } from '@/types/api';

type Metric = 'maxWeight' | 'maxVolume';

interface Props {
  planId: number;
}

export function ProgressTab({ planId }: Props) {
  const [metric, setMetric] = useState<Metric>('maxWeight');
  const exercises = useExercisesWithLogs(planId);

  if (exercises.isPending) {
    return <p className="text-muted-foreground">Carregando…</p>;
  }
  if (exercises.isError) {
    return <p className="text-destructive">{extractApiError(exercises.error)}</p>;
  }
  if (exercises.data.length === 0) {
    return (
      <EmptyState
        icon={TrendingUp}
        message="Sem dados ainda. Registre algumas séries para visualizar seu progresso."
      />
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex gap-2 rounded-xl bg-muted p-1">
        <Button
          variant={metric === 'maxWeight' ? 'primary' : 'ghost'}
          size="sm"
          onClick={() => setMetric('maxWeight')}
          className={cn('flex-1', metric === 'maxWeight' && 'shadow-sm')}
        >
          Carga máxima
        </Button>
        <Button
          variant={metric === 'maxVolume' ? 'primary' : 'ghost'}
          size="sm"
          onClick={() => setMetric('maxVolume')}
          className={cn('flex-1', metric === 'maxVolume' && 'shadow-sm')}
        >
          Volume
        </Button>
      </div>

      {exercises.data.map((ex) => (
        <ExerciseChart
          key={ex.id}
          planId={planId}
          exercise={ex}
          metric={metric}
        />
      ))}
    </div>
  );
}

function ExerciseChart({
  planId,
  exercise,
  metric,
}: {
  planId: number;
  exercise: ExerciseSummary;
  metric: Metric;
}) {
  const { data, isPending, isError } = useExerciseProgress(planId, exercise.id);

  return (
    <Card>
      <CardHeader>
        <CardTitle>{exercise.name}</CardTitle>
      </CardHeader>
      <CardContent>
        {isPending && <p className="text-sm text-muted-foreground">Carregando…</p>}
        {isError && <p className="text-sm text-destructive">Erro ao carregar dados</p>}
        {data && data.points.length === 0 && (
          <p className="text-sm text-muted-foreground">Sem dados.</p>
        )}
        {data && data.points.length > 0 && (
          <div className="h-56 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart
                data={data.points.map((p) => ({
                  ...p,
                  label: format(new Date(p.date), 'dd/MM', { locale: ptBR }),
                }))}
                margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
              >
                <defs>
                  <linearGradient id={`grad-${exercise.id}`} x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="hsl(var(--primary))" stopOpacity={0.4} />
                    <stop offset="100%" stopColor="hsl(var(--primary))" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" vertical={false} />
                <XAxis dataKey="label" tick={{ fontSize: 11 }} stroke="hsl(var(--muted-foreground))" />
                <YAxis tick={{ fontSize: 11 }} stroke="hsl(var(--muted-foreground))" />
                <Tooltip
                  contentStyle={{
                    background: 'hsl(var(--card))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: 12,
                    fontSize: 12,
                  }}
                  formatter={(value: number) =>
                    metric === 'maxWeight'
                      ? [`${value} kg`, 'Carga máxima']
                      : [`${value}`, 'Volume']
                  }
                  labelFormatter={(label) => `Data: ${label}`}
                />
                <Area
                  type="monotone"
                  dataKey={metric}
                  stroke="hsl(var(--primary))"
                  strokeWidth={2.5}
                  fill={`url(#grad-${exercise.id})`}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
