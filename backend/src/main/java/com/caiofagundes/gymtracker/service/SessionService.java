/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.caiofagundes.gymtracker.service;

import com.caiofagundes.gymtracker.common.NotFoundException;
import com.caiofagundes.gymtracker.domain.Exercise;
import com.caiofagundes.gymtracker.domain.PlannedSet;
import com.caiofagundes.gymtracker.domain.SetLog;
import com.caiofagundes.gymtracker.domain.Workout;
import com.caiofagundes.gymtracker.domain.WorkoutSession;
import com.caiofagundes.gymtracker.repository.ExerciseRepository;
import com.caiofagundes.gymtracker.repository.PlannedSetRepository;
import com.caiofagundes.gymtracker.repository.SetLogRepository;
import com.caiofagundes.gymtracker.repository.WorkoutRepository;
import com.caiofagundes.gymtracker.repository.WorkoutSessionRepository;
import com.caiofagundes.gymtracker.repository.WorkoutPlanRepository;
import com.caiofagundes.gymtracker.web.dto.SessionDtos;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    private final WorkoutSessionRepository sessions;
    private final WorkoutRepository workouts;
    private final ExerciseRepository exercises;
    private final PlannedSetRepository plannedSets;
    private final SetLogRepository setLogs;
    private final WorkoutPlanRepository plans;

    public SessionService(
            WorkoutSessionRepository sessions,
            WorkoutRepository workouts,
            ExerciseRepository exercises,
            PlannedSetRepository plannedSets,
            SetLogRepository setLogs,
            WorkoutPlanRepository plans) {
        this.sessions = sessions;
        this.workouts = workouts;
        this.exercises = exercises;
        this.plannedSets = plannedSets;
        this.setLogs = setLogs;
        this.plans = plans;
    }

    @Transactional
    public SessionDtos.StartSessionResponse start(Long userId, Long workoutId) {
        Workout workout = this.workouts.findByIdAndOwner(workoutId, userId).orElseThrow(() -> new NotFoundException("Treino n\u00e3o encontrado"));
        WorkoutSession s = WorkoutSession.builder().plan(workout.getPlan()).workout(workout).startedAt(OffsetDateTime.now()).build();
        s = (WorkoutSession)this.sessions.save(s);
        return new SessionDtos.StartSessionResponse(s.getId());
    }

    @Transactional(readOnly=true)
    public SessionDtos.SessionResponse getSession(Long userId, Long sessionId) {
        WorkoutSession s = this.sessions.findByIdAndOwner(sessionId, userId).orElseThrow(() -> new NotFoundException("Sess\u00e3o n\u00e3o encontrada"));
        List<Exercise> exs = this.exercises.findByWorkoutIdOrderByOrderIndexAscIdAsc(s.getWorkout().getId());
        List<SetLog> currentLogs = this.setLogs.findBySessionIdOrderByLoggedAtAsc(sessionId);
        HashMap<Long, SetLog> currentByPlannedSet = new HashMap<Long, SetLog>();
        for (SetLog log : currentLogs) {
            currentByPlannedSet.put(log.getPlannedSet().getId(), log);
        }
        List<SessionDtos.SessionExerciseNode> nodes = exs.stream().map(ex -> {
            List<PlannedSet> ps = this.plannedSets.findByExerciseIdOrderByOrderIndexAscIdAsc(ex.getId());
            List<SessionDtos.SessionPlannedSet> setNodes = ps.stream().map(p -> {
                SessionDtos.LastLog last = this.setLogs.findLastForPlannedSet(p.getId(), sessionId).map(l -> new SessionDtos.LastLog(l.getWeightKg(), l.getPerformedReps(), l.getLoggedAt())).orElse(null);
                SetLog cur = (SetLog)currentByPlannedSet.get(p.getId());
                SessionDtos.CurrentLog currentLog = cur == null ? null : new SessionDtos.CurrentLog(cur.getId(), cur.getWeightKg(), cur.getPerformedReps(), cur.getLoggedAt());
                return new SessionDtos.SessionPlannedSet(p.getId(), p.getType(), p.getRepsMin(), p.getRepsMax(), p.getOrderIndex(), last, currentLog);
            }).toList();
            return new SessionDtos.SessionExerciseNode(ex.getId(), ex.getName(), ex.getOrderIndex(), setNodes);
        }).toList();
        return new SessionDtos.SessionResponse(s.getId(), s.getPlan().getId(), s.getWorkout().getId(), s.getWorkout().getName(), s.getStartedAt(), s.getFinishedAt(), nodes);
    }

    @Transactional(readOnly=true)
    public SessionDtos.ExerciseHistoryResponse exerciseHistory(Long userId, Long sessionId, Long exerciseId) {
        WorkoutSession session = this.sessions.findByIdAndOwner(sessionId, userId).orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
        Exercise exercise = this.exercises.findByIdAndOwner(exerciseId, userId).orElseThrow(() -> new NotFoundException("Exercício não encontrado"));
        if (!exercise.getWorkout().getId().equals(session.getWorkout().getId())) {
            throw new NotFoundException("Exercício não encontrado nesta sessão");
        }
        List<SetLog> logs = this.setLogs.findHistoryForExercise(
                session.getWorkout().getId(),
                exercise.getId(),
                exercise.getName(),
                sessionId,
                userId);
        LinkedHashMap<Long, List<SetLog>> bySession = new LinkedHashMap<Long, List<SetLog>>();
        for (SetLog log : logs) {
            bySession.computeIfAbsent(log.getSession().getId(), k -> new ArrayList<SetLog>()).add(log);
        }
        List<SessionDtos.HistorySession> history = bySession.entrySet().stream().limit(8L).map(entry -> {
            List<SetLog> sessionLogs = entry.getValue();
            WorkoutSession past = sessionLogs.get(0).getSession();
            OffsetDateTime date = past.getFinishedAt() != null ? past.getFinishedAt() : past.getStartedAt();
            List<SessionDtos.HistorySet> sets = sessionLogs.stream().map(l -> new SessionDtos.HistorySet(
                    l.getPlannedSet().getOrderIndex(),
                    l.getPlannedSet().getType(),
                    l.getWeightKg(),
                    l.getPerformedReps())).toList();
            return new SessionDtos.HistorySession(past.getId(), date, sets);
        }).toList();
        return new SessionDtos.ExerciseHistoryResponse(exercise.getName(), history);
    }

    @Transactional
    public SessionDtos.SetLogResponse logSet(Long userId, Long sessionId, SessionDtos.SetLogRequest req) {
        WorkoutSession session = this.sessions.findByIdAndOwner(sessionId, userId).orElseThrow(() -> new NotFoundException("Sess\u00e3o n\u00e3o encontrada"));
        PlannedSet plannedSet = this.plannedSets.findByIdAndOwner(req.plannedSetId(), userId).orElseThrow(() -> new NotFoundException("S\u00e9rie n\u00e3o encontrada"));
        SetLog log = SetLog.builder().session(session).plannedSet(plannedSet).weightKg(req.weightKg()).performedReps(req.performedReps()).build();
        log = (SetLog)this.setLogs.save(log);
        return this.toLogResponse(log);
    }

    @Transactional
    public SessionDtos.SetLogResponse updateLog(Long userId, Long logId, SessionDtos.SetLogUpdate req) {
        SetLog log = this.setLogs.findByIdAndOwner(logId, userId).orElseThrow(() -> new NotFoundException("Registro n\u00e3o encontrado"));
        log.setWeightKg(req.weightKg());
        log.setPerformedReps(req.performedReps());
        return this.toLogResponse(log);
    }

    @Transactional
    public void deleteLog(Long userId, Long logId) {
        SetLog log = this.setLogs.findByIdAndOwner(logId, userId).orElseThrow(() -> new NotFoundException("Registro n\u00e3o encontrado"));
        this.setLogs.delete(log);
    }

    @Transactional
    public void finish(Long userId, Long sessionId) {
        WorkoutSession s = this.sessions.findByIdAndOwner(sessionId, userId).orElseThrow(() -> new NotFoundException("Sess\u00e3o n\u00e3o encontrada"));
        if (s.getFinishedAt() == null) {
            s.setFinishedAt(OffsetDateTime.now());
        }
    }

    private SessionDtos.SetLogResponse toLogResponse(SetLog log) {
        return new SessionDtos.SetLogResponse(log.getId(), log.getPlannedSet().getId(), log.getWeightKg(), log.getPerformedReps(), log.getLoggedAt());
    }

    @Transactional(readOnly=true)
    public List<SessionDtos.SessionSummary> listByPlan(Long userId, Long planId) {
        this.plans.findByIdAndUserId(planId, userId).orElseThrow(() -> new NotFoundException("Ficha não encontrada"));
        return this.sessions.summarizeByPlan(planId, userId).stream().map(row -> new SessionDtos.SessionSummary(
                ((Number) row[0]).longValue(),
                ((Number) row[1]).longValue(),
                (String) row[2],
                toOffset(row[3]),
                toOffset(row[4]),
                ((Number) row[5]).longValue())).toList();
    }

    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        WorkoutSession s = this.sessions.findByIdAndOwner(sessionId, userId).orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
        this.sessions.delete(s);
    }

    private static OffsetDateTime toOffset(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof OffsetDateTime odt) {
            return odt;
        }
        if (value instanceof Timestamp ts) {
            return ts.toInstant().atOffset(ZoneOffset.UTC);
        }
        if (value instanceof Instant instant) {
            return instant.atOffset(ZoneOffset.UTC);
        }
        if (value instanceof LocalDateTime ldt) {
            return ldt.atOffset(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Tipo de data inesperado: " + value.getClass().getName());
    }
}

