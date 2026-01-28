package org.example.backend.service;

import org.example.backend.model.HabitCompletion;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.HabitCompletionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitCompletionServiceTest {

    private final HabitCompletionRepository habitCompletionRepository = mock(HabitCompletionRepository.class);
    private final HabitCompletionService habitCompletionService = new HabitCompletionService(habitCompletionRepository);

    private UserHabit createMockUserHabit() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setHabitName("Make your bed");
        userHabit.setDifficulty(HabitDifficulty.EASY);
        userHabit.setFrequency(HabitFrequency.DAILY);
        return userHabit;
    }

    @Test
    void recordCompletion_savesAndReturnsCompletion() {
        UserHabit userHabit = createMockUserHabit();

        when(habitCompletionRepository.save(any(HabitCompletion.class))).thenAnswer(invocation -> {
            HabitCompletion saved = invocation.getArgument(0);
            saved.setId("completion123");
            return saved;
        });

        HabitCompletion result = habitCompletionService.recordCompletion("user123", userHabit);

        assertNotNull(result);
        assertEquals("completion123", result.getId());
        assertEquals("user123", result.getUserId());
        assertEquals("uh123", result.getUserHabitId());
        assertEquals("habit123", result.getHabitId());
        assertEquals("Make your bed", result.getHabitName());
        assertEquals(HabitDifficulty.EASY, result.getDifficulty());
        assertEquals(HabitFrequency.DAILY, result.getFrequency());
        assertEquals(25, result.getXpEarned());
        assertNotNull(result.getCompletionDate());

        verify(habitCompletionRepository).save(any(HabitCompletion.class));
    }

    @Test
    void recordCompletion_setsCorrectXpBasedOnDifficulty() {
        UserHabit mediumHabit = createMockUserHabit();
        mediumHabit.setDifficulty(HabitDifficulty.MEDIUM);

        when(habitCompletionRepository.save(any(HabitCompletion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HabitCompletion result = habitCompletionService.recordCompletion("user123", mediumHabit);

        assertEquals(50, result.getXpEarned());
    }

    @Test
    void recordCompletion_hardDifficulty_gives100Xp() {
        UserHabit hardHabit = createMockUserHabit();
        hardHabit.setDifficulty(HabitDifficulty.HARD);

        when(habitCompletionRepository.save(any(HabitCompletion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HabitCompletion result = habitCompletionService.recordCompletion("user123", hardHabit);

        assertEquals(100, result.getXpEarned());
    }

    @Test
    void getCompletionsForWeek_returnsCompletionsForWeek() {
        HabitCompletion completion1 = new HabitCompletion();
        completion1.setId("c1");
        completion1.setUserId("user123");
        completion1.setCompletionDate("2026-01-27");

        HabitCompletion completion2 = new HabitCompletion();
        completion2.setId("c2");
        completion2.setUserId("user123");
        completion2.setCompletionDate("2026-01-28");

        when(habitCompletionRepository.findByUserIdAndWeek("user123", "2026-01-26", "2026-02-01"))
                .thenReturn(List.of(completion1, completion2));

        List<HabitCompletion> result = habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 1, 28));

        assertEquals(2, result.size());
        assertEquals("c1", result.get(0).getId());
        assertEquals("c2", result.get(1).getId());
        verify(habitCompletionRepository).findByUserIdAndWeek("user123", "2026-01-26", "2026-02-01");
    }

    @Test
    void getCompletionsForWeek_calculatesCorrectMondayForWednesday() {
        when(habitCompletionRepository.findByUserIdAndWeek(any(), any(), any())).thenReturn(List.of());

        habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 1, 28));

        verify(habitCompletionRepository).findByUserIdAndWeek("user123", "2026-01-26", "2026-02-01");
    }

    @Test
    void getCompletionsForWeek_calculatesCorrectMondayForMonday() {
        when(habitCompletionRepository.findByUserIdAndWeek(any(), any(), any())).thenReturn(List.of());

        habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 1, 26));

        verify(habitCompletionRepository).findByUserIdAndWeek("user123", "2026-01-26", "2026-02-01");
    }

    @Test
    void getCompletionsForWeek_calculatesCorrectMondayForSunday() {
        when(habitCompletionRepository.findByUserIdAndWeek(any(), any(), any())).thenReturn(List.of());

        habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 2, 1));

        verify(habitCompletionRepository).findByUserIdAndWeek("user123", "2026-01-26", "2026-02-01");
    }

    @Test
    void getCompletionsForWeek_whenNoCompletions_returnsEmptyList() {
        when(habitCompletionRepository.findByUserIdAndWeek(any(), any(), any())).thenReturn(List.of());

        List<HabitCompletion> result = habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 1, 28));

        assertTrue(result.isEmpty());
    }
}