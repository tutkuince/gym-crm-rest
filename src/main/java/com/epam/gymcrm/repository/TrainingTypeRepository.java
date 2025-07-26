package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}
