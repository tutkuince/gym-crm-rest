package com.epam.gymcrm.db.repository;

import com.epam.gymcrm.db.entity.TrainingEntity;
import com.epam.gymcrm.domain.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingEntity, Long>, JpaSpecificationExecutor<TrainingEntity> {
}
