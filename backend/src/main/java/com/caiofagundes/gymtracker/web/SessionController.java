/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.tags.Tag
 *  jakarta.validation.Valid
 *  org.springframework.http.HttpStatus
 *  org.springframework.web.bind.annotation.DeleteMapping
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.PutMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.ResponseStatus
 *  org.springframework.web.bind.annotation.RestController
 */
package com.caiofagundes.gymtracker.web;

import com.caiofagundes.gymtracker.security.CurrentUser;
import com.caiofagundes.gymtracker.service.SessionService;
import com.caiofagundes.gymtracker.web.dto.SessionDtos;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api"})
@Tag(name="Sessions")
public class SessionController {
    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping(value={"/workouts/{workoutId}/sessions"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public SessionDtos.StartSessionResponse start(@PathVariable Long workoutId) {
        return this.service.start(CurrentUser.requireUserId(), workoutId);
    }

    @GetMapping(value={"/sessions/{id}"})
    public SessionDtos.SessionResponse getSession(@PathVariable Long id) {
        return this.service.getSession(CurrentUser.requireUserId(), id);
    }

    @GetMapping(value={"/sessions/{id}/exercises/{exerciseId}/history"})
    public SessionDtos.ExerciseHistoryResponse exerciseHistory(
            @PathVariable Long id, @PathVariable Long exerciseId) {
        return this.service.exerciseHistory(CurrentUser.requireUserId(), id, exerciseId);
    }

    @PostMapping(value={"/sessions/{id}/logs"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public SessionDtos.SetLogResponse logSet(@PathVariable Long id, @Valid @RequestBody SessionDtos.SetLogRequest req) {
        return this.service.logSet(CurrentUser.requireUserId(), id, req);
    }

    @PutMapping(value={"/logs/{logId}"})
    public SessionDtos.SetLogResponse updateLog(@PathVariable Long logId, @Valid @RequestBody SessionDtos.SetLogUpdate req) {
        return this.service.updateLog(CurrentUser.requireUserId(), logId, req);
    }

    @DeleteMapping(value={"/logs/{logId}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deleteLog(@PathVariable Long logId) {
        this.service.deleteLog(CurrentUser.requireUserId(), logId);
    }

    @PostMapping(value={"/sessions/{id}/finish"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void finish(@PathVariable Long id) {
        this.service.finish(CurrentUser.requireUserId(), id);
    }

    @GetMapping(value={"/plans/{planId}/sessions"})
    public List<SessionDtos.SessionSummary> listByPlan(@PathVariable Long planId) {
        return this.service.listByPlan(CurrentUser.requireUserId(), planId);
    }

    @DeleteMapping(value={"/sessions/{id}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long id) {
        this.service.deleteSession(CurrentUser.requireUserId(), id);
    }
}

