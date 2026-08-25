import { Pencil } from 'lucide-react';
import { useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { Layout } from '@/components/Layout';
import { Button } from '@/components/ui/button';
import { extractApiError } from '@/lib/api/client';
import { usePlanDetail } from '@/lib/api/hooks';
import { ProgressTab } from './tabs/ProgressTab';
import { StartTab } from './tabs/StartTab';
import { WorkoutsTab } from './tabs/WorkoutsTab';
import { cn } from '@/lib/utils';

type Tab = 'workouts' | 'start' | 'progress';

const TABS: { id: Tab; label: string }[] = [
  { id: 'workouts', label: 'Treinos' },
  { id: 'start', label: 'Iniciar' },
  { id: 'progress', label: 'Progresso' },
];

export function PlanDetailPage() {
  const { id } = useParams<{ id: string }>();
  const planId = Number(id);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const initialTab = searchParams.get('tab');
  const [tab, setTab] = useState<Tab>(
    initialTab === 'start' || initialTab === 'progress' || initialTab === 'workouts'
      ? initialTab
      : 'workouts',
  );
  const { data, isPending, isError, error } = usePlanDetail(planId);

  return (
    <Layout
      title={data?.name ?? '...'}
      back="/"
      actions={
        <Button
          type="button"
          variant="ghost"
          size="icon"
          onClick={() => navigate(`/plans/${planId}/edit`)}
          aria-label="Editar"
        >
          <Pencil className="h-4 w-4" />
        </Button>
      }
    >
      <div className="mb-5 flex gap-1 rounded-xl bg-muted p-1">
        {TABS.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className={cn(
              'flex-1 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
              tab === t.id
                ? 'bg-card text-foreground shadow-sm'
                : 'text-muted-foreground hover:text-foreground',
            )}
          >
            {t.label}
          </button>
        ))}
      </div>

      {isPending && <p className="text-muted-foreground">Carregando…</p>}
      {isError && <p className="text-destructive">{extractApiError(error)}</p>}
      {data && tab === 'workouts' && <WorkoutsTab planId={planId} plan={data} />}
      {data && tab === 'start' && <StartTab planId={planId} plan={data} />}
      {data && tab === 'progress' && <ProgressTab planId={planId} />}
    </Layout>
  );
}
