package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.exception.HabitNotFoundException;
import org.example.backend.model.Habit;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;

    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    public Habit getHabitById(String id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit with id " + id + " not found"));
    }

    public List<Habit> getHabitsByCategory(String category) {
        return habitRepository.findByCategory(category);
    }

    public List<Habit> getHabitsByDifficulty(HabitDifficulty difficulty) {
        return habitRepository.findByDifficulty(difficulty);
    }

    public Habit getRandomDailyHabit() {
        return habitRepository.findRandomDailyHabit()
                .orElseThrow(() -> new HabitNotFoundException("No daily habit found"));
    }

    public Habit getRandomWeeklyHabit() {
        return habitRepository.findRandomWeeklyHabit()
                .orElseThrow(() -> new HabitNotFoundException("No weekly habit found"));
    }

    public Habit getRandomDailyHabitExcluding(List<String> excludeIds) {
        if(excludeIds == null || excludeIds.isEmpty()) {
            return getRandomDailyHabit();
        }
        return habitRepository.findRandomDailyHabitExcluding(excludeIds)
                .orElseThrow(() -> new HabitNotFoundException("No daily habit found"));
    }

    public Habit getRandomWeeklyHabitExcluding(List<String> excludeIds) {
        if(excludeIds == null || excludeIds.isEmpty()) {
            return getRandomWeeklyHabit();
        }
        return habitRepository.findRandomWeeklyHabitExcluding(excludeIds)
                .orElseThrow(() -> new HabitNotFoundException("No weekly habit found"));
    }
}
