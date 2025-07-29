package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Old password is required") String oldPassword,
        @NotBlank(message = "New password cannot be blank") String newPassword
) {
}
