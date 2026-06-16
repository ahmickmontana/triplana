package com.triplana.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_change_requests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailChangeRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "new_email", length = 255)
    private String newEmail;

    @Column(name = "verification_token_hash", nullable = false, length = 255, updatable = false)
    private String verificationTokenHash;

    @Column(name = "confirmation_token_hash", length = 255)
    private String confirmationTokenHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
