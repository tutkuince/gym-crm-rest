package com.epam.gymcrm.db.repository;

import com.epam.gymcrm.db.entity.TraineeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<TraineeEntity, Long> {

    @Query("SELECT t FROM TraineeEntity t LEFT JOIN FETCH t.trainers WHERE t.id = :id")
    Optional<TraineeEntity> findByIdWithTrainers(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM TraineeEntity t LEFT JOIN FETCH t.trainers")
    List<TraineeEntity> findAllWithTrainers();

    Optional<TraineeEntity> findByUserUsername(String username);

    @Query("SELECT t FROM TraineeEntity t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username")
    Optional<TraineeEntity> findByUserUsernameWithTrainers(@Param("username") String username);
}
