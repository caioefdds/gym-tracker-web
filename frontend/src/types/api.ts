export type SetType = 'WARMUP' | 'WORKING' | 'STRENGTH';

export const setTypeLabel: Record<SetType, string> = {
  WARMUP: 'Aquecimento',
  WORKING: 'Trabalho',
  STRENGTH: 'Força',
};

export const setTypeShortLabel: Record<SetType, string> = {
  WARMUP: 'Aquec.',
  WORKING: 'Trab.',
  STRENGTH: 'Força',
};

export interface User {
  id: number;
  email: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Plan {
  id: number;
  name: string;
  startDate: string;
  isActive: boolean;
}

export interface PlannedSetNode {
  id: number;
  type: SetType;
  repsMin: number;
  repsMax: number;
  orderIndex: number;
}

export interface ExerciseNode {
  id: number;
  name: string;
  orderIndex: number;
  plannedSets: PlannedSetNode[];
}

export interface WorkoutNode {
  id: number;
  name: string;
  orderIndex: number;
  exercises: ExerciseNode[];
}

export interface PlanDetail {
  id: number;
  name: string;
  startDate: string;
  isActive: boolean;
  workouts: WorkoutNode[];
}

export interface LastLog {
  weightKg: number;
  performedReps: number;
  loggedAt: string;
}

export interface CurrentLog {
  id: number;
  weightKg: number;
  performedReps: number;
  loggedAt: string;
}

export interface SessionPlannedSet {
  plannedSetId: number;
  type: SetType;
  repsMin: number;
  repsMax: number;
  orderIndex: number;
  lastTime: LastLog | null;
  currentLog: CurrentLog | null;
}

export interface SessionExerciseNode {
  exerciseId: number;
  exerciseName: string;
  orderIndex: number;
  sets: SessionPlannedSet[];
}

export interface SessionResponse {
  sessionId: number;
  planId: number;
  workoutId: number;
  workoutName: string;
  startedAt: string;
  finishedAt: string | null;
  exercises: SessionExerciseNode[];
}

export interface HistorySet {
  orderIndex: number;
  type: SetType;
  weightKg: number;
  performedReps: number;
}

export interface HistorySession {
  sessionId: number;
  date: string;
  sets: HistorySet[];
}

export interface ExerciseHistoryResponse {
  exerciseName: string;
  sessions: HistorySession[];
}

export interface SessionSummary {
  sessionId: number;
  workoutId: number;
  workoutName: string;
  startedAt: string;
  finishedAt: string | null;
  loggedSets: number;
}

export interface SetLogResponse {
  id: number;
  plannedSetId: number;
  weightKg: number;
  performedReps: number;
  loggedAt: string;
}

export interface ProgressPoint {
  date: string;
  maxWeight: number;
  maxVolume: number;
}

export interface ExerciseProgress {
  exerciseId: number;
  exerciseName: string | null;
  points: ProgressPoint[];
}

export interface ExerciseSummary {
  id: number;
  name: string;
  workoutId: number;
}

export interface ApiError {
  status: number;
  message: string;
  fieldErrors?: Record<string, string>;
}
