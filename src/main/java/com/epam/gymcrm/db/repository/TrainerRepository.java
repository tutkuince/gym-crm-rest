package com.epam.gymcrm.db.repository;

import com.epam.gymcrm.domain.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM TrainerEntity t LEFT JOIN FETCH t.trainees WHERE t.id = :id")
    Optional<Trainer> findByIdWithTrainees(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM TrainerEntity t LEFT JOIN FETCH t.trainees")
    List<Trainer> findAllWithTrainees();

    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t FROM TrainerEntity t LEFT JOIN FETCH t.trainees WHERE t.user.username = :username")
    Optional<Trainer> findByUserUsernameWithTrainees(@Param("username") String username);

    @Query("SELECT tr FROM TrainerEntity tr WHERE tr.id NOT IN " +
            "(SELECT ttr.id FROM TraineeEntity trn JOIN trn.trainers ttr WHERE trn.id = :traineeId)")
    List<Trainer> findUnassignedTrainersForTrainee(@Param("traineeId") Long traineeId);
}
