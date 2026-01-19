package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Habit;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends MongoRepository<Habit, String> {


    List<Habit> findByCategory(String category);

    List<Habit> findByDifficulty(HabitDifficulty difficulty);

    @Aggregation(pipeline =  {
            "{ $match: {difficulty:  { $in:  ['EASY', 'MEDIUM']}, frequency:  'DAILY'} }",
            "{ $sample:  {size:  1 } }"
    })
    Optional<Habit> findRandomDailyHabit();

   @Aggregation(pipeline = {
           "{ $match:  {difficulty: { $in:  ['EASY', 'MEDIUM', 'HARD']}, frequency: 'WEEKLY' } }",
           "{ $sample:  {size:  1}}"
   })
    Optional<Habit> findRandomWeeklyHabit();

   @Aggregation(pipeline = {
           "{ $match:  {difficulty:  { $in:  ['EASY', 'MEDIUM']}, frequency:  'DAILY', _id:  { $nin:  ?0} } }",
           "{ $sample: {size:  1} }"
   })
    Optional<Habit> findRandomDailyHabitExcluding(List<String> excludedIds);

   @Aggregation(pipeline = {
           "{ $match:  {difficulty:  { $in:  ['EASY','MEDIUM', 'HARD']}, frequency: 'WEEKLY', _id: { $nin:  ?0} }}",
           "{ $sample:  {size:  1}}"
   })
   Optional<Habit> findRandomWeeklyHabitExcluding(List<String> excludedIds);
}


