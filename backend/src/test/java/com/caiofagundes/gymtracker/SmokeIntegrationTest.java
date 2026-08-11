/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caiofagundes.gymtracker.user.UserRepository
 *  org.assertj.core.api.Assertions
 *  org.junit.jupiter.api.Test
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.caiofagundes.gymtracker;

import com.caiofagundes.gymtracker.AbstractIntegrationTest;
import com.caiofagundes.gymtracker.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SmokeIntegrationTest
extends AbstractIntegrationTest {
    @Autowired
    private UserRepository users;

    SmokeIntegrationTest() {
    }

    @Test
    void contextLoadsAndRepositoriesAreUsable() {
        Assertions.assertThat((long)this.users.count()).isGreaterThanOrEqualTo(0L);
    }
}

