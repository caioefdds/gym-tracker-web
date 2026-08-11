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
import com.caiofagundes.gymtracker.service.PlanService;
import com.caiofagundes.gymtracker.web.dto.PlanDtos;
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
@Tag(name="Plans")
public class PlanController {
    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    @GetMapping(value={"/plans"})
    public List<PlanDtos.PlanResponse> listPlans() {
        return this.service.listPlans(CurrentUser.requireUserId());
    }

    @PostMapping(value={"/plans"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public PlanDtos.PlanResponse createPlan(@Valid @RequestBody PlanDtos.PlanRequest req) {
        return this.service.createPlan(CurrentUser.requireUserId(), req);
    }

    @GetMapping(value={"/plans/{id}"})
    public PlanDtos.PlanDetailResponse getPlanDetail(@PathVariable Long id) {
        return this.service.getPlanDetail(CurrentUser.requireUserId(), id);
    }

    @PutMapping(value={"/plans/{id}"})
    public PlanDtos.PlanResponse updatePlan(@PathVariable Long id, @Valid @RequestBody PlanDtos.PlanRequest req) {
        return this.service.updatePlan(CurrentUser.requireUserId(), id, req);
    }

    @DeleteMapping(value={"/plans/{id}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id) {
        this.service.deletePlan(CurrentUser.requireUserId(), id);
    }

    @PostMapping(value={"/plans/{planId}/workouts"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public PlanDtos.WorkoutNode createWorkout(@PathVariable Long planId, @Valid @RequestBody PlanDtos.WorkoutRequest req) {
        return this.service.createWorkout(CurrentUser.requireUserId(), planId, req);
    }

    @PutMapping(value={"/workouts/{id}"})
    public PlanDtos.WorkoutNode updateWorkout(@PathVariable Long id, @Valid @RequestBody PlanDtos.WorkoutUpdate req) {
        return this.service.updateWorkout(CurrentUser.requireUserId(), id, req);
    }

    @DeleteMapping(value={"/workouts/{id}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deleteWorkout(@PathVariable Long id) {
        this.service.deleteWorkout(CurrentUser.requireUserId(), id);
    }

    @PostMapping(value={"/workouts/{workoutId}/exercises"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public PlanDtos.ExerciseNode createExercise(@PathVariable Long workoutId, @Valid @RequestBody PlanDtos.ExerciseRequest req) {
        return this.service.createExercise(CurrentUser.requireUserId(), workoutId, req);
    }

    @PutMapping(value={"/exercises/{id}"})
    public PlanDtos.ExerciseNode updateExercise(@PathVariable Long id, @Valid @RequestBody PlanDtos.ExerciseUpdate req) {
        return this.service.updateExercise(CurrentUser.requireUserId(), id, req);
    }

    @DeleteMapping(value={"/exercises/{id}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deleteExercise(@PathVariable Long id) {
        this.service.deleteExercise(CurrentUser.requireUserId(), id);
    }

    @PostMapping(value={"/exercises/{exerciseId}/planned-sets"})
    @ResponseStatus(value=HttpStatus.CREATED)
    public PlanDtos.PlannedSetNode createPlannedSet(@PathVariable Long exerciseId, @Valid @RequestBody PlanDtos.PlannedSetRequest req) {
        return this.service.createPlannedSet(CurrentUser.requireUserId(), exerciseId, req);
    }

    @PutMapping(value={"/planned-sets/{id}"})
    public PlanDtos.PlannedSetNode updatePlannedSet(@PathVariable Long id, @Valid @RequestBody PlanDtos.PlannedSetUpdate req) {
        return this.service.updatePlannedSet(CurrentUser.requireUserId(), id, req);
    }

    @DeleteMapping(value={"/planned-sets/{id}"})
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void deletePlannedSet(@PathVariable Long id) {
        this.service.deletePlannedSet(CurrentUser.requireUserId(), id);
    }
}

