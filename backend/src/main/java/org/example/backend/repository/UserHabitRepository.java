package org.example.backend.repository;

import org.example.backend.model.UserHabit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHabitRepository extends MongoRepository<UserHabit, String> {

    List<UserHabit> findByUserId(String userId);

    List<UserHabit> findByUserIdAndActiveTrue(String userId);

    Optional<UserHabit> findByUserIdAndHabitId(String userId, String habitId);

    boolean existsByUserIdAndHabitId(String userId, String habitId);
}
