package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.TrainerDto;

import java.util.Objects;
import java.util.stream.Collectors;

public class TrainerMapper {

    public static Trainer toTrainer(TrainerDto trainerDto) {
        if (Objects.isNull(trainerDto))
            return null;

        Trainer trainer = new Trainer();
        trainer.setId(trainerDto.getId());

        User user = new User();
        user.setFirstName(trainerDto.getFirstName());
        user.setLastName(trainerDto.getLastName());
        user.setUsername(trainerDto.getUsername());
        user.setActive(trainerDto.getActive());

        trainer.setUser(user);
        trainer.setSpecialization(trainerDto.getSpecialization());

        return trainer;
    }

    public static TrainerDto toTrainerDto(Trainer trainer) {
        if (Objects.isNull(trainer) || Objects.isNull(trainer.getUser()))
            return null;

        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setId(trainer.getId());
        trainerDto.setFirstName(trainer.getUser().getFirstName());
        trainerDto.setLastName(trainer.getUser().getLastName());
        trainerDto.setUsername(trainer.getUser().getUsername());
        trainerDto.setActive(trainer.getUser().getActive());
        trainerDto.setSpecialization(trainer.getSpecialization());

        // Map ManyToMany trainees as traineeIds
        if (Objects.nonNull(trainer.getTrainees()) && !trainer.getTrainees().isEmpty()) {
            trainerDto.setTraineeIds(
                    trainer.getTrainees().stream()
                            .map(Trainee::getId)
                            .collect(Collectors.toList())
            );
        } else {
            trainerDto.setTraineeIds(null);
        }

        return trainerDto;
    }
}
