package com.epam.gymcrm.api.payload.response;

import java.util.List;

public record UnassignedActiveTrainerListResponse(
        List<UnassignedActiveTrainerResponse> trainers
) {
}
