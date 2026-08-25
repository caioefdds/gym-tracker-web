package com.caiofagundes.gymtracker.user;

import com.caiofagundes.gymtracker.config.AppProperties;
import com.caiofagundes.gymtracker.mail.EmailSender;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_TTL_HOURS = 1;
    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final AppProperties props;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(
            UserRepository users,
            PasswordResetTokenRepository tokens,
            PasswordEncoder passwordEncoder,
            EmailSender emailSender,
            AppProperties props) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.props = props;
    }

    @Transactional
    public void forgot(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        this.users.findByEmail(email).ifPresent(this::issueToken);
    }

    @Transactional
    public void reset(String rawToken, String newPassword) {
        String hash = sha256(rawToken.trim());
        PasswordResetToken token = this.tokens.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Link inválido ou expirado"));
        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        User user = token.getUser();
        user.setPasswordHash(this.passwordEncoder.encode(newPassword));
        this.users.save(user);
        token.setUsedAt(OffsetDateTime.now());
        this.tokens.save(token);
        this.tokens.deleteByUserAndUsedAtIsNull(user);
    }

    private void issueToken(User user) {
        this.tokens.deleteByUserAndUsedAtIsNull(user);
        byte[] bytes = new byte[32];
        this.random.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setTokenHash(sha256(rawToken));
        token.setExpiresAt(OffsetDateTime.now().plusHours(TOKEN_TTL_HOURS));
        this.tokens.save(token);

        String base = this.props.publicUrl().replaceAll("/$", "");
        String link = base + "/reset-password?token=" + rawToken;
        try {
            this.emailSender.send(
                    user.getEmail(),
                    "Redefinir senha — Gym Tracker",
                    """
                    Olá %s,

                    Recebemos um pedido para redefinir a senha da sua conta no Gym Tracker.
                    Abra o link abaixo (válido por %d hora):

                    %s

                    Se você não pediu isso, ignore este e-mail.
                    """.formatted(user.getName(), TOKEN_TTL_HOURS, link));
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", user.getEmail(), e);
        }
    }

    static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
