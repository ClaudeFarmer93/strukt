package org.example.backend.model;

public enum HabitDifficulty {
    EASY(50),
    MEDIUM(75),
    HARD(100);

    private final int baseXp;

    HabitDifficulty(int baseXp) {
        this.baseXp = baseXp;
    }

    public int getBaseXp() {
        return baseXp;
    }
}
