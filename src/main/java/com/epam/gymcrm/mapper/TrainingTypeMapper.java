package com.epam.gymcrm.mapper;

import com.epam.gymcrm.domain.model.TrainingType;
import com.epam.gymcrm.dto.TrainingTypeDto;

import java.util.Objects;

public class TrainingTypeMapper {

    public static TrainingType toTrainingType(TrainingTypeDto dto) {
        if (Objects.isNull(dto)) return null;
        TrainingType type = new TrainingType();
        type.setId(dto.getId());
        type.setTrainingTypeName(dto.getTrainingTypeName());
        return type;
    }

    public static TrainingTypeDto toTrainingTypeDto(TrainingType type) {
        if (Objects.isNull(type)) return null;
        TrainingTypeDto dto = new TrainingTypeDto();
        dto.setId(type.getId());
        dto.setTrainingTypeName(type.getTrainingTypeName());
        return dto;
    }
}
