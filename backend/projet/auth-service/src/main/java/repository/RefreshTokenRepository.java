package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.RefreshToken;
import entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
