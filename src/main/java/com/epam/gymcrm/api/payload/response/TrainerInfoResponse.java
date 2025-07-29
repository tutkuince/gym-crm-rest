package com.epam.gymcrm.api.payload.response;

public record TrainerInfoResponse(
        String username,
        String firstName,
        String lastName,
        Long specialization
) {
}
