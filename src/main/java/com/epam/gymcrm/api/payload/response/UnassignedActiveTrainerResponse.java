package com.epam.gymcrm.api.payload.response;

public record UnassignedActiveTrainerResponse(
        String username,
        String firstName,
        String lastName,
        Long specialization
) {
}
