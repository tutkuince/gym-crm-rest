package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;

public record TrainerUsernameRequest(
        @NotBlank String trainerUsername
) {
}
