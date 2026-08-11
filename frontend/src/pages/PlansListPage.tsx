import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Dumbbell, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { Layout } from '@/components/Layout';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { extractApiError } from '@/lib/api/client';
import { usePlans } from '@/lib/api/hooks';

export function PlansListPage() {
  const navigate = useNavigate();
  const { data, isPending, isError, error } = usePlans();

  return (
    <Layout
      title="Minhas Fichas"
      actions={
        <Button size="sm" onClick={() => navigate('/plans/new')}>
          <Plus className="h-4 w-4" />
          <span className="hidden sm:inline">Nova ficha</span>
        </Button>
      }
    >
      {isPending && <p className="text-muted-foreground">Carregando…</p>}
      {isError && <p className="text-destructive">{extractApiError(error)}</p>}
      {data && data.length === 0 && (
        <EmptyState
          icon={Dumbbell}
          message="Você ainda não tem nenhuma ficha. Toque em + para criar a primeira."
          actionLabel="Nova ficha"
          onAction={() => navigate('/plans/new')}
        />
      )}
      {data && data.length > 0 && (
        <div className="grid gap-3 sm:grid-cols-2">
          {data.map((p) => (
            <Link key={p.id} to={`/plans/${p.id}`}>
              <Card className="flex h-full items-center gap-4 p-5 transition-colors hover:border-primary/40">
                <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-xl bg-accent">
                  <Dumbbell className="h-6 w-6 text-accent-foreground" />
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="truncate font-semibold">{p.name}</h3>
                  <p className="text-xs text-muted-foreground">
                    Início: {format(new Date(p.startDate), 'dd/MM/yyyy', { locale: ptBR })}
                  </p>
                </div>
                {p.isActive && <Badge variant="primary">Ativa</Badge>}
              </Card>
            </Link>
          ))}
        </div>
      )}
    </Layout>
  );
}
