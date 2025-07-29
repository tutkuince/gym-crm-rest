package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerRegistrationRequest(
        @NotBlank(message = "First name is required.") String firstName,
        @NotBlank(message = "Last name is required.") String lastName,
        @NotNull(message = "Specialization is required.")
        @Min(value = 1, message = "Specialization id must be positive.")
        Long specialization
) {
}
