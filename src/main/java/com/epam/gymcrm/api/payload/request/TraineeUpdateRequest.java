package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeUpdateRequest(
        @NotBlank String username,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        String dateOfBirth,
        String address,
        @NotNull(message = "isActive is required")
        boolean isActive
) {
}
