package com.project.repositories;

import com.project.models.PasswordResetToken;
import com.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser_Email(String email);

    PasswordResetToken findByUser(User user);
}