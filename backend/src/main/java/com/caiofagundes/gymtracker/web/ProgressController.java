/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.tags.Tag
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.caiofagundes.gymtracker.web;

import com.caiofagundes.gymtracker.security.CurrentUser;
import com.caiofagundes.gymtracker.service.ProgressService;
import com.caiofagundes.gymtracker.web.dto.ProgressDtos;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api"})
@Tag(name="Progress")
public class ProgressController {
    private final ProgressService service;

    public ProgressController(ProgressService service) {
        this.service = service;
    }

    @GetMapping(value={"/plans/{planId}/progress/exercises"})
    public List<ProgressDtos.ExerciseSummary> listExercisesWithLogs(@PathVariable Long planId) {
        return this.service.exercisesWithLogs(CurrentUser.requireUserId(), planId);
    }

    @GetMapping(value={"/plans/{planId}/progress"})
    public ProgressDtos.ExerciseProgress progress(@PathVariable Long planId, @RequestParam Long exerciseId) {
        return this.service.progress(CurrentUser.requireUserId(), planId, exerciseId);
    }
}

