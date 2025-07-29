package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String specialization
) {
}
