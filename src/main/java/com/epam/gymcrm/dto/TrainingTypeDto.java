package com.epam.gymcrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class TrainingTypeDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Training type name is required")
    private String trainingTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
