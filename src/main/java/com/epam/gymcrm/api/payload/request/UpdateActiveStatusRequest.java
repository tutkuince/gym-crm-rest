package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateActiveStatusRequest(
        @NotBlank String username,
        @NotNull boolean isActive
) {
}
