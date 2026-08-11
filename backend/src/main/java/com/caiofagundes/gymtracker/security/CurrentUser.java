/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.context.SecurityContextHolder
 */
package com.caiofagundes.gymtracker.security;

import com.caiofagundes.gymtracker.security.AuthUser;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {
    }

    public static Long requireUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthUser) {
            AuthUser auth = (AuthUser)principal;
            return auth.getUserId();
        }
        throw new IllegalStateException("No authenticated user in context");
    }
}

