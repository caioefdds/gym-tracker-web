/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Email
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.Size
 */
package com.caiofagundes.gymtracker.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record AuthResponse(String token, UserResponse user) {
    }

    public record UserResponse(Long id, String email, String name) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min=8, max=100) @NotBlank @Size(min=8, max=100) String password, @NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name) {
    }
}

