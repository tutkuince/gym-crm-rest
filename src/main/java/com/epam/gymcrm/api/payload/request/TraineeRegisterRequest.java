package com.epam.gymcrm.api.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

public record TraineeRegisterRequest(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        String dateOfBirth,
        String address
) {
}
