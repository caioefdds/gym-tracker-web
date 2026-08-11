/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caiofagundes.gymtracker.user.AuthService
 *  com.caiofagundes.gymtracker.web.dto.AuthDtos$AuthResponse
 *  com.caiofagundes.gymtracker.web.dto.AuthDtos$LoginRequest
 *  com.caiofagundes.gymtracker.web.dto.AuthDtos$RegisterRequest
 *  org.assertj.core.api.Assertions
 *  org.junit.jupiter.api.Test
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.caiofagundes.gymtracker;

import com.caiofagundes.gymtracker.AbstractIntegrationTest;
import com.caiofagundes.gymtracker.user.AuthService;
import com.caiofagundes.gymtracker.web.dto.AuthDtos;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthFlowIntegrationTest
extends AbstractIntegrationTest {
    @Autowired
    private AuthService auth;

    AuthFlowIntegrationTest() {
    }

    @Test
    void registerThenLoginIssuesToken() {
        AuthDtos.AuthResponse registered = this.auth.register(new AuthDtos.RegisterRequest("test+" + System.nanoTime() + "@example.com", "secret-password", "Test User"));
        Assertions.assertThat((String)registered.token()).isNotBlank();
        Assertions.assertThat((Long)registered.user().id()).isNotNull();
        AuthDtos.AuthResponse loggedIn = this.auth.login(new AuthDtos.LoginRequest(registered.user().email(), "secret-password"));
        Assertions.assertThat((String)loggedIn.token()).isNotBlank();
        Assertions.assertThat((Long)loggedIn.user().id()).isEqualTo((Object)registered.user().id());
    }
}

