package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Habit;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.UserHabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserHabitService {

    private final UserHabitRepository userHabitRepository;
    private final HabitService habitService;

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

   /* public void removeUserHabit(String userId, String habitId) {
        UserHabit userHabit = userHabitRepository.findByUserIdAndHabitId(userId, habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found in user's habit-list"));
        userHabit.setActive(false);
        userHabitRepository.save(userHabit);
    }
    */
}
