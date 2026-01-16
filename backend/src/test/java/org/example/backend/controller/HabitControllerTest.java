package org.example.backend.controller;

import org.example.backend.model.Habit;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.repository.AppUserRepository;
import org.example.backend.security.SecurityConfig;
import org.example.backend.service.HabitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitController.class)
@Import(SecurityConfig.class)
public class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitService habitService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void getAllHabits_returnsHabitList() throws Exception {
        Habit habit1 = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);
        habit1.setId("habit1");
        Habit habit2 = new Habit("Gym workout", "Push your limits", "Fitness", HabitDifficulty.HARD, HabitFrequency.WEEKLY);
        habit2.setId("habit2");

        when(habitService.getAllHabits()).thenReturn(List.of(habit1, habit2));

        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("habit1"))
                .andExpect(jsonPath("$[0].name").value("Make your bed"))
                .andExpect(jsonPath("$[0].difficulty").value("EASY"))
                .andExpect(jsonPath("$[0].frequency").value("DAILY"))
                .andExpect(jsonPath("$[1].id").value("habit2"))
                .andExpect(jsonPath("$[1].name").value("Gym workout"));
    }

    @Test
    void getAllHabits_whenEmpty_returnsEmptyList() throws Exception {
        when(habitService.getAllHabits()).thenReturn(List.of());

        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRandomDailyHabit_returnsHabit() throws Exception {
        Habit habit = new Habit("Make your bed", "Start the day right", "Morning Routine", HabitDifficulty.EASY, HabitFrequency.DAILY);
        habit.setId("daily123");

        when(habitService.getRandomDailyHabit()).thenReturn(habit);

        mockMvc.perform(get("/api/habits/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("daily123"))
                .andExpect(jsonPath("$.name").value("Make your bed"))
                .andExpect(jsonPath("$.difficulty").value("EASY"))
                .andExpect(jsonPath("$.frequency").value("DAILY"));
    }

    @Test
    void getRandomWeeklyHabit_returnsHabit() throws Exception {
        Habit habit = new Habit("Meal prep", "Prepare for the week", "Health", HabitDifficulty.HARD, HabitFrequency.WEEKLY);
        habit.setId("weekly123");

        when(habitService.getRandomWeeklyHabit()).thenReturn(habit);

        mockMvc.perform(get("/api/habits/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("weekly123"))
                .andExpect(jsonPath("$.name").value("Meal prep"))
                .andExpect(jsonPath("$.difficulty").value("HARD"))
                .andExpect(jsonPath("$.frequency").value("WEEKLY"));
    }
}