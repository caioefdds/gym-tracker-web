package com.caiofagundes.gymtracker.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    void deleteByUserAndUsedAtIsNull(User user);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
