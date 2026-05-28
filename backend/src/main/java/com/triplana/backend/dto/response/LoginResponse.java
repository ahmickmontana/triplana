package com.triplana.backend.dto.response;

import com.triplana.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for login attempts.
 * Status can be "success" or "unverified" to direct React to the correct page.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String status;
    private UserResponse user;

    /**
     * Creates a successful login response with the authenticated user's details.
     *
     * @param user the authenticated user entity
     * @return a LoginResponse with status "success" and user details
     */
    public static LoginResponse success(User user) {
        return new LoginResponse("success", UserResponse.from(user));
    }

    /**
     * Creates an unverified login response indicating the account needs verification.
     *
     * @return a LoginResponse with status "unverified" and no user details
     */
    public static LoginResponse unverified() {
        return new LoginResponse("unverified", null);
    }
}