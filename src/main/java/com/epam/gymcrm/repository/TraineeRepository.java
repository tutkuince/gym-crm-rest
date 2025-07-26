package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.id = :id")
    Optional<Trainee> findByIdWithTrainers(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM Trainee t LEFT JOIN FETCH t.trainers")
    List<Trainee> findAllWithTrainers();

    Optional<Trainee> findByUserUsername(String username);

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username")
    Optional<Trainee> findByUserUsernameWithTrainers(@Param("username") String username);
}
