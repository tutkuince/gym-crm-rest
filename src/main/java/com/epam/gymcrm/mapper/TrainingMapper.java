package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.model.Training;
import com.epam.gymcrm.dto.TrainingDto;

import java.util.Objects;

public class TrainingMapper {

    public static Training toTraining(TrainingDto trainingDto) {
        if (Objects.isNull(trainingDto))
            return null;

        Training training = new Training();
        training.setId(trainingDto.getId());
        training.setTrainingName(trainingDto.getTrainingName());
        training.setTrainingDate(trainingDto.getTrainingDate());
        training.setTrainingDuration(trainingDto.getTrainingDuration());

        return training;
    }

    public static TrainingDto toTrainingDto(Training training) {
        if (Objects.isNull(training))
            return null;

        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setId(training.getId());
        trainingDto.setTrainingName(training.getTrainingName());
        trainingDto.setTrainingDate(training.getTrainingDate());
        trainingDto.setTrainingDuration(training.getTrainingDuration());

        // Relationship IDs
        trainingDto.setTrainerId(
                Objects.nonNull(training.getTrainer()) ? training.getTrainer().getId() : null
        );
        trainingDto.setTraineeId(
                Objects.nonNull(training.getTrainee()) ? training.getTrainee().getId() : null
        );
        trainingDto.setTrainingTypeId(
                Objects.nonNull(training.getTrainingType()) ? training.getTrainingType().getId() : null
        );

        return trainingDto;
    }
}
