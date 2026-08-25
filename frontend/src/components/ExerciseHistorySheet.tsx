import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { History, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { extractApiError } from '@/lib/api/client';
import { useExerciseHistory } from '@/lib/api/hooks';
import { cn, formatWeight } from '@/lib/utils';
import { setTypeShortLabel, type HistorySet } from '@/types/api';

interface Props {
  open: boolean;
  onClose: () => void;
  sessionId: number;
  exerciseId: number;
  currentOrderIndex: number;
  onUse: (set: HistorySet) => void;
}

export function ExerciseHistorySheet({
  open,
  onClose,
  sessionId,
  exerciseId,
  currentOrderIndex,
  onUse,
}: Props) {
  const { data, isPending, isError, error } = useExerciseHistory(
    sessionId,
    exerciseId,
    open,
  );

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50">
      <button
        type="button"
        className="absolute inset-0 bg-black/40"
        aria-label="Fechar histórico"
        onClick={onClose}
      />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="history-title"
        className="absolute inset-x-0 bottom-0 max-h-[80vh] overflow-y-auto rounded-t-3xl border-t border-border bg-background px-4 pb-8 pt-3 shadow-lg"
      >
        <div className="mx-auto mb-3 h-1 w-10 rounded-full bg-muted" />
        <div className="mb-4 flex items-center gap-2">
          <History className="h-5 w-5 text-primary" />
          <h2 id="history-title" className="flex-1 text-base font-semibold">
            Histórico
          </h2>
          <Button type="button" variant="ghost" size="icon" onClick={onClose} aria-label="Fechar">
            <X className="h-5 w-5" />
          </Button>
        </div>
        <p className="mb-4 text-xs text-muted-foreground">
          Últimos treinos deste exercício. Toque numa série para copiar carga e reps.
        </p>

        {isPending && <p className="text-sm text-muted-foreground">Carregando…</p>}
        {isError && <p className="text-sm text-destructive">{extractApiError(error)}</p>}
        {data && data.sessions.length === 0 && (
          <p className="text-sm text-muted-foreground">
            Ainda não há treinos anteriores deste exercício nesta ficha.
          </p>
        )}
        {data && data.sessions.length > 0 && (
          <div className="space-y-4">
            {data.sessions.map((session) => (
              <section key={session.sessionId} className="rounded-2xl border border-border p-3">
                <h3 className="mb-2 text-sm font-semibold capitalize">
                  {format(new Date(session.date), "EEE, dd/MM/yyyy", { locale: ptBR })}
                </h3>
                <ul className="space-y-1.5">
                  {session.sets.map((set, i) => {
                    const sameSlot = set.orderIndex === currentOrderIndex;
                    return (
                      <li key={`${session.sessionId}-${set.orderIndex}-${i}`}>
                        <button
                          type="button"
                          onClick={() => onUse(set)}
                          className={cn(
                            'flex w-full items-center gap-3 rounded-xl px-3 py-2 text-left text-sm transition-colors',
                            sameSlot
                              ? 'bg-primary/10 ring-1 ring-primary/30'
                              : 'bg-muted/60 hover:bg-muted',
                          )}
                        >
                          <span className="w-16 shrink-0 text-xs text-muted-foreground">
                            Série {i + 1}
                          </span>
                          <span className="w-14 shrink-0 text-xs text-muted-foreground">
                            {setTypeShortLabel[set.type]}
                          </span>
                          <span className="flex-1 font-semibold">
                            {formatWeight(set.weightKg)} kg
                          </span>
                          <span className="font-semibold">{set.performedReps} reps</span>
                        </button>
                      </li>
                    );
                  })}
                </ul>
              </section>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
