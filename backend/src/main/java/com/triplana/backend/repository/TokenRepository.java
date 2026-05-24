package com.triplana.backend.repository;

import com.triplana.backend.entity.Token;
import com.triplana.backend.entity.User;
import com.triplana.backend.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenHash(String tokenHash);
    boolean existsByTokenHash(String tokenHash);
    Optional<Token> findByUserAndType(User user, TokenType type);
    void deleteAllByUserAndType(User user, TokenType type);
}