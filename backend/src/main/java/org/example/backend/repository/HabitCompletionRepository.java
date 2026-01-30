package org.example.backend.repository;

import org.example.backend.model.HabitCompletion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitCompletionRepository extends MongoRepository<HabitCompletion, String> {

    @Query("{ 'userId': ?0, 'completionDate': { $gte: ?1, $lte: ?2 } }")
    List<HabitCompletion> findByUserIdAndWeek(String userId, String startDate, String endDate);

}
