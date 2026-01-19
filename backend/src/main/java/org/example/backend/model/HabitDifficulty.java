package org.example.backend.model;

public enum HabitDifficulty {
    EASY(25),
    MEDIUM(50),
    HARD(100);

    private final int baseXp;

    HabitDifficulty(int baseXp) {
        this.baseXp = baseXp;
    }

    public int getBaseXp() {
        return baseXp;
    }
}
