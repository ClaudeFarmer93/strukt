package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection ="user_habits")
public class UserHabit {

    @Id
    private String id;

    private String userId;
    private String habitId;

    private String habitName;
    private HabitDifficulty difficulty;
    private HabitFrequency frequency;

    private int currentStreak;
    private int longestStreak;
    private LocalDate lastCompletedDate;
    private int totalCompletions;
    private int totalXpEarned;

    private boolean active;

    public UserHabit(String userId, Habit habit) {
        this.userId = userId;
        this.habitId = habit.getId();
        this.habitName = habit.getName();
        this.difficulty = habit.getDifficulty();
        this.frequency = habit.getFrequency();
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.lastCompletedDate = null;
        this.totalCompletions = 0;
        this.totalXpEarned = 0;
        this.active = true;
    }
}
