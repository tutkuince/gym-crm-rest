package com.epam.gymcrm.db.repository.specification;

import com.epam.gymcrm.domain.model.Training;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TrainingSpecification {

    public static Specification<Training> traineeUsername(String username) {
        return (root, query, cb) ->
                username == null ? null : cb.equal(root.get("trainee").get("user").get("username"), username);
    }

    public static Specification<Training> fromDate(LocalDate from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("trainingDate"), from);
    }

    public static Specification<Training> toDate(LocalDate to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("trainingDate"), to);
    }

    public static Specification<Training> trainerName(String trainerName) {
        return (root, query, cb) ->
                trainerName == null ? null : cb.equal(root.get("trainer").get("user").get("firstName"), trainerName);
    }

    public static Specification<Training> trainingType(String trainingTypeName) {
        return (root, query, cb) -> {
            if (trainingTypeName == null) return null;
            Join<Object, Object> trainingTypeJoin = root.join("trainingType");
            return cb.equal(trainingTypeJoin.get("trainingTypeName"), trainingTypeName);
        };
    }


    public static Specification<Training> traineeName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.equal(root.get("trainee").get("user").get("firstName"), name);
    }

    public static Specification<Training> trainerUsername(String username) {
        return (root, query, cb) ->
                username == null ? null : cb.equal(root.get("trainer").get("user").get("username"), username);
    }


}
