package org.example.backend.service;

import org.example.backend.model.Habit;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.repository.HabitRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HabitServiceTest {

    private final HabitRepository habitRepository = mock(HabitRepository.class);
    private final HabitService habitService = new HabitService(habitRepository);

    @Test
    void getAllHabits_returnsAllHabits() {
        Habit habit1 = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);
        habit1.setId("habit1");
        Habit habit2 = new Habit("Gym workout", "Push your limits", "Fitness", HabitDifficulty.HARD, HabitFrequency.WEEKLY);
        habit2.setId("habit2");

        when(habitRepository.findAll()).thenReturn(List.of(habit1, habit2));

        List<Habit> result = habitService.getAllHabits();

        assertEquals(2, result.size());
        assertEquals("habit1", result.get(0).getId());
        assertEquals("habit2", result.get(1).getId());
        verify(habitRepository).findAll();
    }

    @Test
    void getAllHabits_whenEmpty_returnsEmptyList() {
        when(habitRepository.findAll()).thenReturn(List.of());

        List<Habit> result = habitService.getAllHabits();

        assertTrue(result.isEmpty());
        verify(habitRepository).findAll();
    }

    @Test
    void getHabitById_returnsHabit() {
        Habit habit = new Habit("Read for 20 minutes", "Expand your mind", "Personal Growth", HabitDifficulty.MEDIUM, HabitFrequency.DAILY);
        habit.setId("habit123");

        when(habitRepository.findById("habit123")).thenReturn(Optional.of(habit));

        Habit result = habitService.getHabitById("habit123");

        assertNotNull(result);
        assertEquals("habit123", result.getId());
        assertEquals("Read for 20 minutes", result.getName());
        verify(habitRepository).findById("habit123");
    }

    @Test
    void getHabitById_whenNotFound_throwsException() {
        when(habitRepository.findById("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitService.getHabitById("nonexistent");
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(habitRepository).findById("nonexistent");
    }

    @Test
    void getHabitsByCategory_returnsMatchingHabits() {
        Habit habit1 = new Habit("Brush teeth", "Oral hygiene", "Health", HabitDifficulty.EASY, HabitFrequency.DAILY);
        Habit habit2 = new Habit("Take vitamins", "Stay healthy", "Health", HabitDifficulty.EASY, HabitFrequency.DAILY);

        when(habitRepository.findByCategory("Health")).thenReturn(List.of(habit1, habit2));

        List<Habit> result = habitService.getHabitsByCategory("Health");

        assertEquals(2, result.size());
        verify(habitRepository).findByCategory("Health");
    }

    @Test
    void getHabitsByDifficulty_returnsMatchingHabits() {
        Habit habit = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);

        when(habitRepository.findByDifficulty(HabitDifficulty.EASY)).thenReturn(List.of(habit));

        List<Habit> result = habitService.getHabitsByDifficulty(HabitDifficulty.EASY);

        assertEquals(1, result.size());
        assertEquals(HabitDifficulty.EASY, result.get(0).getDifficulty());
        verify(habitRepository).findByDifficulty(HabitDifficulty.EASY);
    }

    @Test
    void getRandomDailyHabit_returnsHabit() {
        Habit habit = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);
        habit.setId("daily123");

        when(habitRepository.findRandomDailyHabit()).thenReturn(Optional.of(habit));

        Habit result = habitService.getRandomDailyHabit();

        assertNotNull(result);
        assertEquals("daily123", result.getId());
        assertEquals(HabitFrequency.DAILY, result.getFrequency());
        verify(habitRepository).findRandomDailyHabit();
    }

    @Test
    void getRandomDailyHabit_whenNoneFound_throwsException() {
        when(habitRepository.findRandomDailyHabit()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitService.getRandomDailyHabit();
        });

        assertTrue(exception.getMessage().contains("daily"));
        verify(habitRepository).findRandomDailyHabit();
    }

    @Test
    void getRandomWeeklyHabit_returnsHabit() {
        Habit habit = new Habit("Meal prep", "Prepare for the week", "Health", HabitDifficulty.HARD, HabitFrequency.WEEKLY);
        habit.setId("weekly123");

        when(habitRepository.findRandomWeeklyHabit()).thenReturn(Optional.of(habit));

        Habit result = habitService.getRandomWeeklyHabit();

        assertNotNull(result);
        assertEquals("weekly123", result.getId());
        assertEquals(HabitFrequency.WEEKLY, result.getFrequency());
        verify(habitRepository).findRandomWeeklyHabit();
    }

    @Test
    void getRandomWeeklyHabit_whenNoneFound_throwsException() {
        when(habitRepository.findRandomWeeklyHabit()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habitService.getRandomWeeklyHabit();
        });

        assertTrue(exception.getMessage().contains("weekly"));
        verify(habitRepository).findRandomWeeklyHabit();
    }
}