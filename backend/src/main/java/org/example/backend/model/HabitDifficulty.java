package org.example.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HabitDifficulty {
    EASY(25),
    MEDIUM(50),
    HARD(100);

    private final int baseXp;
}
