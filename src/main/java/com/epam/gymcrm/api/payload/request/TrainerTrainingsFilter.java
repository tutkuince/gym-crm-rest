package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TrainerTrainingsFilter(
        @NotBlank(message = "Username must not be blank")
        String username,

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "periodFrom must be in the format yyyy-MM-dd")
        String periodFrom,

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "periodTo must be in the format yyyy-MM-dd")
        String periodTo,
        String traineeName
) {
}
