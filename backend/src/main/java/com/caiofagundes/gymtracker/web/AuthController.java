package com.caiofagundes.gymtracker.web;

import com.caiofagundes.gymtracker.security.CurrentUser;
import com.caiofagundes.gymtracker.user.AuthService;
import com.caiofagundes.gymtracker.user.PasswordResetService;
import com.caiofagundes.gymtracker.web.dto.AuthDtos;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        return this.authService.register(req);
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        return this.authService.login(req);
    }

    @PostMapping("/forgot-password")
    public AuthDtos.MessageResponse forgotPassword(@Valid @RequestBody AuthDtos.ForgotPasswordRequest req) {
        this.passwordResetService.forgot(req.email());
        return new AuthDtos.MessageResponse(
                "Se o e-mail estiver cadastrado, você receberá um link para redefinir a senha.");
    }

    @PostMapping("/reset-password")
    public AuthDtos.MessageResponse resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest req) {
        this.passwordResetService.reset(req.token(), req.password());
        return new AuthDtos.MessageResponse("Senha redefinida. Entre com a nova senha.");
    }

    @GetMapping("/me")
    public AuthDtos.UserResponse me() {
        return this.authService.me(CurrentUser.requireUserId());
    }
}
