package org.example.backend.service;

import org.example.backend.model.Habit;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.UserHabitRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserHabitServiceTest {

    private final UserHabitRepository userHabitRepository = mock(UserHabitRepository.class);
    private final HabitService habitService = mock(HabitService.class);
    private final UserHabitService userHabitService = new UserHabitService(userHabitRepository, habitService);

    @Test
    void getUserHabits_returnsActiveHabits() {
        UserHabit userHabit1 = new UserHabit();
        userHabit1.setId("uh1");
        userHabit1.setUserId("user123");
        userHabit1.setHabitId("habit1");
        userHabit1.setHabitName("Make your bed");
        userHabit1.setActive(true);

        UserHabit userHabit2 = new UserHabit();
        userHabit2.setId("uh2");
        userHabit2.setUserId("user123");
        userHabit2.setHabitId("habit2");
        userHabit2.setHabitName("Read for 20 minutes");
        userHabit2.setActive(true);

        when(userHabitRepository.findByUserIdAndActiveTrue("user123")).thenReturn(List.of(userHabit1, userHabit2));

        List<UserHabit> result = userHabitService.getUserHabits("user123");

        assertEquals(2, result.size());
        assertEquals("Make your bed", result.get(0).getHabitName());
        assertEquals("Read for 20 minutes", result.get(1).getHabitName());
        verify(userHabitRepository).findByUserIdAndActiveTrue("user123");
    }

    @Test
    void getUserHabits_whenNoHabits_returnsEmptyList() {
        when(userHabitRepository.findByUserIdAndActiveTrue("user123")).thenReturn(List.of());

        List<UserHabit> result = userHabitService.getUserHabits("user123");

        assertTrue(result.isEmpty());
        verify(userHabitRepository).findByUserIdAndActiveTrue("user123");
    }

    @Test
    void acceptUserHabit_createsUserHabit() {
        Habit habit = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);
        habit.setId("habit123");

        when(userHabitRepository.existsByUserIdAndHabitId("user123", "habit123")).thenReturn(false);
        when(habitService.getHabitById("habit123")).thenReturn(habit);
        when(userHabitRepository.save(any(UserHabit.class))).thenAnswer(invocation -> {
            UserHabit saved = invocation.getArgument(0);
            saved.setId("userhabit123");
            return saved;
        });

        UserHabit result = userHabitService.acceptUserHabit("user123", "habit123");

        assertNotNull(result);
        assertEquals("userhabit123", result.getId());
        assertEquals("user123", result.getUserId());
        assertEquals("habit123", result.getHabitId());
        assertEquals("Make your bed", result.getHabitName());
        assertEquals(HabitDifficulty.EASY, result.getDifficulty());
        assertEquals(HabitFrequency.DAILY, result.getFrequency());
        assertEquals(0, result.getCurrentStreak());
        assertEquals(0, result.getTotalCompletions());
        assertTrue(result.isActive());

        verify(userHabitRepository).existsByUserIdAndHabitId("user123", "habit123");
        verify(habitService).getHabitById("habit123");
        verify(userHabitRepository).save(any(UserHabit.class));
    }

    @Test
    void acceptUserHabit_whenAlreadyExists_throwsException() {
        when(userHabitRepository.existsByUserIdAndHabitId("user123", "habit123")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHabitService.acceptUserHabit("user123", "habit123"));

        assertTrue(exception.getMessage().contains("already"));
        verify(userHabitRepository).existsByUserIdAndHabitId("user123", "habit123");
        verify(habitService, never()).getHabitById(any());
        verify(userHabitRepository, never()).save(any());
    }

    @Test
    void deleteUserHabit_deletesHabit() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));

        userHabitService.deleteUserHabit("user123", "habit123");

        verify(userHabitRepository).findByUserIdAndHabitId("user123", "habit123");
        verify(userHabitRepository).delete(userHabit);
    }

    @Test
    void deleteUserHabit_whenNotFound_throwsException() {
        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHabitService.deleteUserHabit("user123", "habit123"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(userHabitRepository).findByUserIdAndHabitId("user123", "habit123");
        verify(userHabitRepository, never()).delete(any());
    }

    @Test
    void completeUserHabit_firstCompletion_setsStreakToOne() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setDifficulty(HabitDifficulty.EASY);
        userHabit.setFrequency(HabitFrequency.DAILY);
        userHabit.setCurrentStreak(0);
        userHabit.setLongestStreak(0);
        userHabit.setTotalCompletions(0);
        userHabit.setTotalXpEarned(0);
        userHabit.setLastCompletedDate(null);

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));
        when(userHabitRepository.save(any(UserHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserHabit result = userHabitService.completeUserHabit("user123", "habit123");

        assertEquals(1, result.getCurrentStreak());
        assertEquals(1, result.getLongestStreak());
        assertEquals(1, result.getTotalCompletions());
        assertEquals(LocalDate.now(), result.getLastCompletedDate());
        verify(userHabitRepository).save(userHabit);
    }

    @Test
    void completeUserHabit_consecutiveDay_incrementsStreak() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setDifficulty(HabitDifficulty.MEDIUM);
        userHabit.setFrequency(HabitFrequency.DAILY);
        userHabit.setCurrentStreak(5);
        userHabit.setLongestStreak(5);
        userHabit.setTotalCompletions(5);
        userHabit.setTotalXpEarned(100);
        userHabit.setLastCompletedDate(LocalDate.now().minusDays(1));

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));
        when(userHabitRepository.save(any(UserHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserHabit result = userHabitService.completeUserHabit("user123", "habit123");

        assertEquals(6, result.getCurrentStreak());
        assertEquals(6, result.getLongestStreak());
        assertEquals(6, result.getTotalCompletions());
    }

    @Test
    void completeUserHabit_missedDay_resetsStreak() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setDifficulty(HabitDifficulty.EASY);
        userHabit.setFrequency(HabitFrequency.DAILY);
        userHabit.setCurrentStreak(5);
        userHabit.setLongestStreak(10);
        userHabit.setTotalCompletions(15);
        userHabit.setTotalXpEarned(150);
        userHabit.setLastCompletedDate(LocalDate.now().minusDays(3));

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));
        when(userHabitRepository.save(any(UserHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserHabit result = userHabitService.completeUserHabit("user123", "habit123");

        assertEquals(1, result.getCurrentStreak());
        assertEquals(10, result.getLongestStreak());
        assertEquals(16, result.getTotalCompletions());
    }

    @Test
    void completeUserHabit_alreadyCompletedToday_throwsException() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setFrequency(HabitFrequency.DAILY);
        userHabit.setLastCompletedDate(LocalDate.now());

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHabitService.completeUserHabit("user123", "habit123"));

        assertTrue(exception.getMessage().contains("already completed"));
        verify(userHabitRepository, never()).save(any());
    }

    @Test
    void completeUserHabit_weeklyHabit_consecutiveWeek_incrementsStreak() {
        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit123");
        userHabit.setDifficulty(HabitDifficulty.HARD);
        userHabit.setFrequency(HabitFrequency.WEEKLY);
        userHabit.setCurrentStreak(3);
        userHabit.setLongestStreak(3);
        userHabit.setTotalCompletions(3);
        userHabit.setTotalXpEarned(150);
        userHabit.setLastCompletedDate(LocalDate.now().minusWeeks(1));

        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.of(userHabit));
        when(userHabitRepository.save(any(UserHabit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserHabit result = userHabitService.completeUserHabit("user123", "habit123");

        assertEquals(4, result.getCurrentStreak());
        assertEquals(4, result.getLongestStreak());
        assertEquals(4, result.getTotalCompletions());
    }

    @Test
    void completeUserHabit_whenNotFound_throwsException() {
        when(userHabitRepository.findByUserIdAndHabitId("user123", "habit123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHabitService.completeUserHabit("user123", "habit123"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(userHabitRepository, never()).save(any());
    }
}