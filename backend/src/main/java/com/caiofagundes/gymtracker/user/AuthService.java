/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.caiofagundes.gymtracker.user;

import com.caiofagundes.gymtracker.common.ConflictException;
import com.caiofagundes.gymtracker.common.UnauthorizedException;
import com.caiofagundes.gymtracker.security.JwtService;
import com.caiofagundes.gymtracker.user.User;
import com.caiofagundes.gymtracker.user.UserRepository;
import com.caiofagundes.gymtracker.web.dto.AuthDtos;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (this.userRepository.existsByEmail(email)) {
            throw new ConflictException("E-mail j\u00e1 cadastrado");
        }
        User user = User.builder().email(email).name(req.name().trim()).passwordHash(this.passwordEncoder.encode((CharSequence)req.password())).build();
        user = (User)this.userRepository.save(user);
        String token = this.jwtService.issueToken(user.getId(), user.getEmail());
        return new AuthDtos.AuthResponse(token, this.toResponse(user));
    }

    @Transactional(readOnly=true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Credenciais inv\u00e1lidas"));
        if (!this.passwordEncoder.matches((CharSequence)req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inv\u00e1lidas");
        }
        String token = this.jwtService.issueToken(user.getId(), user.getEmail());
        return new AuthDtos.AuthResponse(token, this.toResponse(user));
    }

    @Transactional(readOnly=true)
    public AuthDtos.UserResponse me(Long userId) {
        User user = (User)this.userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("Usu\u00e1rio n\u00e3o encontrado"));
        return this.toResponse(user);
    }

    private AuthDtos.UserResponse toResponse(User user) {
        return new AuthDtos.UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}

