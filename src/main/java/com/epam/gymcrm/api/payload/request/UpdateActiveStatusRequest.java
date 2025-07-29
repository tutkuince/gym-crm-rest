package com.epam.gymcrm.api.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateActiveStatusRequest(
        @NotBlank(message = "Username must not be blank")
        String username,
        @NotNull(message = "isActive must not be null")
        Boolean isActive
) {
}
