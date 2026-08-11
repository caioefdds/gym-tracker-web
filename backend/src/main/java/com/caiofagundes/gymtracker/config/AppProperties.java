/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.caiofagundes.gymtracker.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="app")
public record AppProperties(Jwt jwt, Cors cors) {

    public record Jwt(String secret, long expirationHours) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}

