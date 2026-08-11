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
import com.caiofagundes.gymtracker.web.dto.SessionDtos;
import java.time.OffsetDateTime;
import java.util.HashMap;
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

    public SessionService(WorkoutSessionRepository sessions, WorkoutRepository workouts, ExerciseRepository exercises, PlannedSetRepository plannedSets, SetLogRepository setLogs) {
        this.sessions = sessions;
        this.workouts = workouts;
        this.exercises = exercises;
        this.plannedSets = plannedSets;
        this.setLogs = setLogs;
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
}

