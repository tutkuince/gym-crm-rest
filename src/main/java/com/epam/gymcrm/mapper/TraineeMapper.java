package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.TraineeDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

public class TraineeMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static TraineeDto toTraineeDto(Trainee trainee) {
        if (Objects.isNull(trainee) || Objects.isNull(trainee.getUser()))
            return null;

        TraineeDto dto = new TraineeDto();
        dto.setId(trainee.getId());
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        dto.setUsername(trainee.getUser().getUsername());
        dto.setActive(trainee.getUser().getActive());
        dto.setDateOfBirth(
                trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().format(FORMATTER) : null
        );
        dto.setAddress(trainee.getAddress());

        // Map trainers (Many-to-Many relationship) by extracting their IDs
        if (Objects.nonNull(trainee.getTrainers()) && !trainee.getTrainers().isEmpty()) {
            dto.setTrainerIds(
                    trainee.getTrainers().stream()
                            .map(Trainer::getId)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setTrainerIds(null);
        }

        return dto;
    }

    public static Trainee toTrainee(TraineeDto traineeDto) {
        if (Objects.isNull(traineeDto))
            return null;

        Trainee trainee = new Trainee();
        trainee.setId(traineeDto.getId());

        User user = new User();
        user.setFirstName(traineeDto.getFirstName());
        user.setLastName(traineeDto.getLastName());
        user.setUsername(traineeDto.getUsername());
        user.setActive(traineeDto.getActive());

        trainee.setUser(user);

        if (traineeDto.getDateOfBirth() != null && !traineeDto.getDateOfBirth().isBlank()) {
            trainee.setDateOfBirth(LocalDate.parse(traineeDto.getDateOfBirth(), FORMATTER));
        }
        trainee.setAddress(traineeDto.getAddress());

        return trainee;
    }
}
