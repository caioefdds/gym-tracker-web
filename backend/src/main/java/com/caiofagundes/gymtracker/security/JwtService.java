/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.jsonwebtoken.Claims
 *  io.jsonwebtoken.Jwts
 *  io.jsonwebtoken.security.Keys
 *  org.springframework.stereotype.Service
 */
package com.caiofagundes.gymtracker.security;

import com.caiofagundes.gymtracker.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationHours;

    public JwtService(AppProperties props) {
        byte[] secret = props.jwt().secret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor((byte[])secret);
        this.expirationHours = props.jwt().expirationHours();
    }

    public String issueToken(Long userId, String email) {
        Instant now = Instant.now();
        Instant exp = now.plus(this.expirationHours, ChronoUnit.HOURS);
        return Jwts.builder().subject(String.valueOf(userId)).claim("email", (Object)email).issuedAt(Date.from(now)).expiration(Date.from(exp)).signWith((Key)this.key).compact();
    }

    public Long extractUserId(String token) {
        Claims claims = (Claims)Jwts.parser().verifyWith(this.key).build().parseSignedClaims((CharSequence)token).getPayload();
        return Long.valueOf(claims.getSubject());
    }
}

