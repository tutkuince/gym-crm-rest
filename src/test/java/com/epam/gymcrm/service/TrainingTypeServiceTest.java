package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    TrainingType type1, type2;
    TrainingTypeDto updateDto;

    @BeforeEach
    void setUp() {
        type1 = new TrainingType();
        type1.setId(1L);
        type1.setTrainingTypeName("Yoga");

        type2 = new TrainingType();
        type2.setId(2L);
        type2.setTrainingTypeName("Pilates");

        updateDto = new TrainingTypeDto();
        updateDto.setId(1L);
        updateDto.setTrainingTypeName("Updated Yoga");
    }

    @Test
    void shouldReturnAllTrainingTypes() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of(type1, type2));
        List<TrainingTypeDto> result = trainingTypeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga", result.get(0).getTrainingTypeName());
        assertEquals("Pilates", result.get(1).getTrainingTypeName());
        verify(trainingTypeRepository).findAll();
    }
}