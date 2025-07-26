package com.epam.gymcrm.dto;

import java.util.List;

public class UpdateTraineeTrainersRequest {
    private List<Long> trainerIds;

    public List<Long> getTrainerIds() {
        return trainerIds;
    }

    public void setTrainerIds(List<Long> trainerIds) {
        this.trainerIds = trainerIds;
    }
}
