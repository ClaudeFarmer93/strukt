package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "habit_completion")
public class HabitCompletion {

    @Id
    private String id;
    private String userId;
    private String userHabitId;
    private String habitId;
    private String habitName;
    private HabitDifficulty difficulty;
    private HabitFrequency frequency;
    private String completionDate;
    private int xpEarned;


    public HabitCompletion(String userId, UserHabit userHabit) {
        this.userId = userId;
        this.userHabitId = userHabit.getId();
        this.habitId = userHabit.getHabitId();
        this.habitName = userHabit.getHabitName();
        this.difficulty = userHabit.getDifficulty();
        this.frequency = userHabit.getFrequency();
        this.completionDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.xpEarned = userHabit.getDifficulty().getBaseXp();

    }
}
