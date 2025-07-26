package com.epam.gymcrm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TrainingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Trainee ID is required")
    private Long traineeId;

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotBlank(message = "Training name is required")
    private String trainingName;

    @NotNull(message = "TrainingType ID is required")
    private Long trainingTypeId;

    @NotNull(message = "Training date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime trainingDate;

    @Min(value = 1, message = "Training duration must be at least 1 minute")
    private int trainingDuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public Long getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDateTime trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
