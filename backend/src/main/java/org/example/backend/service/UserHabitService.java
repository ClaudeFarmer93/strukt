package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.exception.HabitAlreadyExistsException;
import org.example.backend.exception.HabitNotFoundException;
import org.example.backend.model.Habit;
import org.example.backend.model.HabitFrequency;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.UserHabitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserHabitService {

    private final UserHabitRepository userHabitRepository;
    private final HabitService habitService;
    private final HabitCompletionService habitCompletionService;


    public List<UserHabit> getUserHabits(String userId) {
        return userHabitRepository.findByUserIdAndActiveTrue(userId);
    }

    public UserHabit acceptUserHabit(String userId, String habitId) {
        if(userHabitRepository.existsByUserIdAndHabitId(userId, habitId)) {
            throw new RuntimeException("Habit already exists");
        }

        Habit habit = habitService.getHabitById(habitId);

        UserHabit userHabit = new UserHabit(userId, habit);
        return  userHabitRepository.save(userHabit);
    }

    public void deleteUserHabit(String userId, String habitId) {
        UserHabit userHabit = userHabitRepository.findByUserIdAndHabitId(userId,habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found in user's habit-list"));
        userHabitRepository.delete(userHabit);
    }

    public UserHabit completeUserHabit(String userId, String habitId) {
        UserHabit userHabit = userHabitRepository.findByUserIdAndHabitId(userId, habitId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found in user's habitlist"));
        if(isAlreadyCompletedForPeriod(userHabit)) {
            throw new HabitAlreadyExistsException("Habit already completed for this " + (userHabit.getFrequency() == HabitFrequency.DAILY ? "day" : "week"));
        }

        LocalDate today = LocalDate.now();
        LocalDate lastCompleted = userHabit.getLastCompletedDate();

        if(lastCompleted != null && isConsecutivePeriod(userHabit, lastCompleted, today)) {
            userHabit.setCurrentStreak(userHabit.getCurrentStreak() + 1);
        } else{
            userHabit.setCurrentStreak(1);
        }
        if(userHabit.getCurrentStreak() > userHabit.getLongestStreak()) {
            userHabit.setLongestStreak(userHabit.getCurrentStreak());
        }
        userHabit.setLastCompletedDate(today);
        userHabit.setTotalCompletions(userHabit.getTotalCompletions() + 1);
        userHabit.setTotalXpEarned(userHabit.getTotalXpEarned() + userHabit.getDifficulty().getBaseXp());

        habitCompletionService.recordCompletion(userId, userHabit);

        return userHabitRepository.save(userHabit);
    }

    /* public void removeUserHabit(String userId, String habitId) {
         UserHabit userHabit = userHabitRepository.findByUserIdAndHabitId(userId, habitId)
                 .orElseThrow(() -> new RuntimeException("Habit not found in user's habit-list"));
         userHabit.setActive(false);
         userHabitRepository.save(userHabit);
     }
     */
    private boolean isAlreadyCompletedForPeriod(UserHabit userHabit) {
        LocalDate lastCompleted = userHabit.getLastCompletedDate();
        if (lastCompleted == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (userHabit.getFrequency() == HabitFrequency.DAILY) {
            return lastCompleted.equals(today);
        } else {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int lastWeek = lastCompleted.get(weekFields.weekOfWeekBasedYear());
            int lastYear = lastCompleted.get(weekFields.weekBasedYear());
            int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
            int currentYear = today.get(weekFields.weekBasedYear());
            return lastWeek == currentWeek && lastYear == currentYear;
        }
    }

    private boolean isConsecutivePeriod(UserHabit userHabit, LocalDate lastCompleted, LocalDate today) {
        if (userHabit.getFrequency() == HabitFrequency.DAILY) {
            return lastCompleted.plusDays(1).equals(today);
        } else {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int lastWeek = lastCompleted.get(weekFields.weekOfWeekBasedYear());
            int lastYear = lastCompleted.get(weekFields.weekBasedYear());
            int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
            int currentYear = today.get(weekFields.weekBasedYear());

            if (lastYear == currentYear) {
                return currentWeek == lastWeek + 1;
            }
            if (currentYear == lastYear + 1 && currentWeek == 1) {
                // Week 52 or 53
                return lastWeek >= 52;
            }
            return false;
        }
    }
}
