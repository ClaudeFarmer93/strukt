package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "habits")
public class Habit {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private HabitDifficulty difficulty;
    private HabitFrequency frequency;

    public Habit(String name, String description, String categrory,
                 HabitDifficulty difficulty, HabitFrequency frequency) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.frequency = frequency;
    }

    public int getXp() {
        return difficulty != null ? difficulty.getBaseXp() : 0;

    }

}