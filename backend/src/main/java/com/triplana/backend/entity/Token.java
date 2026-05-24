package com.triplana.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, length = 255, updatable = false)
    private String tokenHash;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
