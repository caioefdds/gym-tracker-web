import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from './client';
import type {
  AuthResponse,
  ExerciseHistoryResponse,
  ExerciseNode,
  ExerciseProgress,
  ExerciseSummary,
  Plan,
  PlanDetail,
  PlannedSetNode,
  SessionResponse,
  SessionSummary,
  SetLogResponse,
  SetType,
  User,
  WorkoutNode,
} from '@/types/api';

// Auth ------------------------------------------------------------------

export function useLogin() {
  return useMutation({
    mutationFn: async (input: { email: string; password: string }) => {
      const { data } = await api.post<AuthResponse>('/api/auth/login', input);
      return data;
    },
  });
}

export function useRegister() {
  return useMutation({
    mutationFn: async (input: { email: string; password: string; name: string }) => {
      const { data } = await api.post<AuthResponse>('/api/auth/register', input);
      return data;
    },
  });
}

export function useMe(enabled: boolean) {
  return useQuery({
    queryKey: ['me'],
    enabled,
    queryFn: async () => {
      const { data } = await api.get<User>('/api/auth/me');
      return data;
    },
  });
}

// Plans -----------------------------------------------------------------

export function usePlans() {
  return useQuery({
    queryKey: ['plans'],
    queryFn: async () => (await api.get<Plan[]>('/api/plans')).data,
  });
}

export function usePlanDetail(id: number | null) {
  return useQuery({
    queryKey: ['plans', id],
    enabled: id != null,
    queryFn: async () => (await api.get<PlanDetail>(`/api/plans/${id}`)).data,
  });
}

export function useCreatePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { name: string; startDate: string; isActive: boolean }) =>
      (await api.post<Plan>('/api/plans', input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans'] }),
  });
}

export function useUpdatePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: {
      id: number;
      name: string;
      startDate: string;
      isActive: boolean;
    }) => (await api.put<Plan>(`/api/plans/${input.id}`, input)).data,
    onSuccess: (_data, vars) => {
      qc.invalidateQueries({ queryKey: ['plans'] });
      qc.invalidateQueries({ queryKey: ['plans', vars.id] });
    },
  });
}

export function useDeletePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/api/plans/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans'] }),
  });
}

// Workouts --------------------------------------------------------------

export function useCreateWorkout(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { name: string }) =>
      (await api.post<WorkoutNode>(`/api/plans/${planId}/workouts`, input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useUpdateWorkout(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { id: number; name: string; orderIndex: number }) =>
      (await api.put<WorkoutNode>(`/api/workouts/${input.id}`, input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useDeleteWorkout(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/api/workouts/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

// Exercises -------------------------------------------------------------

export function useCreateExercise(planId: number, workoutId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { name: string }) =>
      (await api.post<ExerciseNode>(`/api/workouts/${workoutId}/exercises`, input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useUpdateExercise(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { id: number; name: string; orderIndex: number }) =>
      (await api.put<ExerciseNode>(`/api/exercises/${input.id}`, input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useDeleteExercise(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/api/exercises/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

// Planned sets ----------------------------------------------------------

export function useCreatePlannedSet(planId: number, exerciseId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { type: SetType; repsMin: number; repsMax: number }) =>
      (
        await api.post<PlannedSetNode>(
          `/api/exercises/${exerciseId}/planned-sets`,
          input,
        )
      ).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useUpdatePlannedSet(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: {
      id: number;
      type: SetType;
      repsMin: number;
      repsMax: number;
      orderIndex: number;
    }) => (await api.put<PlannedSetNode>(`/api/planned-sets/${input.id}`, input)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

export function useDeletePlannedSet(planId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/api/planned-sets/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans', planId] }),
  });
}

// Sessions --------------------------------------------------------------

export function useStartSession() {
  return useMutation({
    mutationFn: async (workoutId: number) =>
      (await api.post<{ sessionId: number }>(`/api/workouts/${workoutId}/sessions`)).data,
  });
}

export function useSession(sessionId: number | null) {
  return useQuery({
    queryKey: ['sessions', sessionId],
    enabled: sessionId != null,
    queryFn: async () =>
      (await api.get<SessionResponse>(`/api/sessions/${sessionId}`)).data,
  });
}

export function useExerciseHistory(
  sessionId: number | null,
  exerciseId: number | null,
  enabled = true,
) {
  return useQuery({
    queryKey: ['sessions', sessionId, 'history', exerciseId],
    enabled: enabled && sessionId != null && exerciseId != null,
    queryFn: async () =>
      (
        await api.get<ExerciseHistoryResponse>(
          `/api/sessions/${sessionId}/exercises/${exerciseId}/history`,
        )
      ).data,
  });
}

export function useLogSet(sessionId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: {
      plannedSetId: number;
      weightKg: number;
      performedReps: number;
    }) =>
      (await api.post<SetLogResponse>(`/api/sessions/${sessionId}/logs`, input)).data,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] });
      qc.invalidateQueries({ queryKey: ['progress'] });
    },
  });
}

export function useUpdateLog(sessionId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: { id: number; weightKg: number; performedReps: number }) =>
      (await api.put<SetLogResponse>(`/api/logs/${input.id}`, input)).data,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] });
      qc.invalidateQueries({ queryKey: ['progress'] });
    },
  });
}

export function useDeleteLog() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (logId: number) => {
      await api.delete(`/api/logs/${logId}`);
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] });
      qc.invalidateQueries({ queryKey: ['progress'] });
    },
  });
}

export function usePlanSessions(planId: number) {
  return useQuery({
    queryKey: ['sessions', 'plan', planId],
    queryFn: async () =>
      (await api.get<SessionSummary[]>(`/api/plans/${planId}/sessions`)).data,
  });
}

export function useDeleteSession() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (sessionId: number) => {
      await api.delete(`/api/sessions/${sessionId}`);
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] });
      qc.invalidateQueries({ queryKey: ['progress'] });
    },
  });
}

export function useFinishSession() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (sessionId: number) => {
      await api.post(`/api/sessions/${sessionId}/finish`);
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] });
    },
  });
}

// Progress --------------------------------------------------------------

export function useExercisesWithLogs(planId: number) {
  return useQuery({
    queryKey: ['progress', 'exercises', planId],
    queryFn: async () =>
      (await api.get<ExerciseSummary[]>(`/api/plans/${planId}/progress/exercises`)).data,
  });
}

export function useExerciseProgress(planId: number, exerciseId: number | null) {
  return useQuery({
    queryKey: ['progress', planId, exerciseId],
    enabled: exerciseId != null,
    queryFn: async () =>
      (
        await api.get<ExerciseProgress>(
          `/api/plans/${planId}/progress`,
          { params: { exerciseId } },
        )
      ).data,
  });
}
