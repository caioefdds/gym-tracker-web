/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 */
package com.caiofagundes.gymtracker.user;

import com.caiofagundes.gymtracker.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String var1);

    public boolean existsByEmail(String var1);
}

