package com.triplana.backend.util;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;


/**
 * Utility class made for generating and hashing secure tokens.
 * This is used for email verification and password reset flows.
 */
@Component
public class TokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * This function generates a secure random token. The raw token is sent
     * to the user via the email sent to them
     * 
     * @return a secure base64 encoded random token string
     */
    public String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashes a raw token using SHA-256 for secure database storage.
     * Only the hash is stored - the raw token is never persisted.
     * 
     * Assisted by Claude AI for this function.
     * 
     * @param rawToken the plain text token to hash
     * @return a base64 encoded SHA-256 hash of the token
     * @throws IllegalStateException if SHA-256 algorithm is unavailable
     */
    public String hashToken(String rawToken) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
