package com.caiofagundes.gymtracker.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.caiofagundes.gymtracker.config.AppProperties;
import com.caiofagundes.gymtracker.mail.EmailSender;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository users;
    @Mock
    private PasswordResetTokenRepository tokens;
    @Mock
    private EmailSender emailSender;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private PasswordResetService service;
    private User user;

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties(
                new AppProperties.Jwt("test-secret-please-only-for-tests-min-32", 24),
                new AppProperties.Cors(List.of("http://localhost:5173")),
                "http://localhost:5173",
                new AppProperties.Mail("noreply@test.local"));
        this.service = new PasswordResetService(this.users, this.tokens, this.encoder, this.emailSender, props);
        this.user = User.builder()
                .id(1L)
                .email("caio@example.com")
                .name("Caio")
                .passwordHash(this.encoder.encode("old-password"))
                .build();
    }

    @Test
    void forgotUnknownEmailDoesNotSend() {
        when(this.users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        this.service.forgot("missing@example.com");

        verify(this.emailSender, never()).send(anyString(), anyString(), anyString());
        verify(this.tokens, never()).save(any());
    }

    @Test
    void forgotSendsLinkAndResetChangesPassword() {
        when(this.users.findByEmail("caio@example.com")).thenReturn(Optional.of(this.user));
        when(this.users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AtomicReference<PasswordResetToken> stored = new AtomicReference<>();
        when(this.tokens.findByTokenHash(anyString())).thenAnswer(inv -> {
            PasswordResetToken token = stored.get();
            if (token != null && token.getTokenHash().equals(inv.getArgument(0))) {
                return Optional.of(token);
            }
            return Optional.empty();
        });
        when(this.tokens.save(any(PasswordResetToken.class))).thenAnswer(inv -> {
            PasswordResetToken token = inv.getArgument(0);
            stored.set(token);
            return token;
        });

        this.service.forgot("caio@example.com");

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(this.emailSender).send(eq("caio@example.com"), anyString(), body.capture());
        String link = body.getValue().lines()
                .filter(line -> line.contains("/reset-password?token="))
                .findFirst()
                .orElseThrow();
        String rawToken = link.substring(link.indexOf("token=") + 6).trim();

        this.service.reset(rawToken, "new-password");

        assertThat(this.encoder.matches("new-password", this.user.getPasswordHash())).isTrue();
        assertThat(stored.get().getUsedAt()).isNotNull();
    }

    @Test
    void resetRejectsUnknownToken() {
        when(this.tokens.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.reset("nope", "new-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void resetRejectsExpiredToken() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(this.user);
        token.setTokenHash(PasswordResetService.sha256("expired-token"));
        token.setExpiresAt(OffsetDateTime.now().minusMinutes(1));
        when(this.tokens.findByTokenHash(token.getTokenHash())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> this.service.reset("expired-token", "new-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }
}
