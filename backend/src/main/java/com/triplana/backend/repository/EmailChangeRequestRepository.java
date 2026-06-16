package com.triplana.backend.repository;

import com.triplana.backend.entity.EmailChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {

    Optional<EmailChangeRequest> findByVerificationTokenHash(String hash);
    Optional<EmailChangeRequest> findByConfirmationTokenHash(String hash) ;
    Optional<EmailChangeRequest> findByUserId(Long userId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}