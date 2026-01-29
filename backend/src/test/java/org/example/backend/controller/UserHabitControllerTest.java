package org.example.backend.controller;

import org.example.backend.model.AppUser;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.model.UserHabit;
import org.example.backend.repository.AppUserRepository;
import org.example.backend.security.SecurityConfig;
import org.example.backend.service.AppUserService;
import org.example.backend.service.UserHabitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserHabitController.class)
@Import(SecurityConfig.class)
public class UserHabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserHabitService userHabitService;

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    private AppUser createMockUser() {
        AppUser user = new AppUser("12345", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        return user;
    }

    @Test
    void getMyHabits_whenAuthenticated_returnsUserHabits() throws Exception {
        AppUser mockUser = createMockUser();

        UserHabit userHabit1 = new UserHabit();
        userHabit1.setId("uh1");
        userHabit1.setUserId("user123");
        userHabit1.setHabitId("habit1");
        userHabit1.setHabitName("Make your bed");
        userHabit1.setDifficulty(HabitDifficulty.EASY);
        userHabit1.setFrequency(HabitFrequency.DAILY);
        userHabit1.setCurrentStreak(5);
        userHabit1.setActive(true);

        UserHabit userHabit2 = new UserHabit();
        userHabit2.setId("uh2");
        userHabit2.setUserId("user123");
        userHabit2.setHabitId("habit2");
        userHabit2.setHabitName("Meal prep");
        userHabit2.setDifficulty(HabitDifficulty.HARD);
        userHabit2.setFrequency(HabitFrequency.WEEKLY);
        userHabit2.setCurrentStreak(2);
        userHabit2.setActive(true);

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(userHabitService.getUserHabits("user123")).thenReturn(List.of(userHabit1, userHabit2));

        mockMvc.perform(get("/api/my-habits")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("uh1"))
                .andExpect(jsonPath("$[0].habitName").value("Make your bed"))
                .andExpect(jsonPath("$[0].difficulty").value("EASY"))
                .andExpect(jsonPath("$[0].currentStreak").value(5))
                .andExpect(jsonPath("$[1].id").value("uh2"))
                .andExpect(jsonPath("$[1].habitName").value("Meal prep"));
    }

    @Test
    void getMyHabits_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/my-habits"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptUserHabit_whenAuthenticated_returnsCreatedUserHabit() throws Exception {
        AppUser mockUser = createMockUser();

        UserHabit userHabit = new UserHabit();
        userHabit.setId("uh123");
        userHabit.setUserId("user123");
        userHabit.setHabitId("habit456");
        userHabit.setHabitName("Read for 20 minutes");
        userHabit.setDifficulty(HabitDifficulty.MEDIUM);
        userHabit.setFrequency(HabitFrequency.DAILY);
        userHabit.setCurrentStreak(0);
        userHabit.setTotalCompletions(0);
        userHabit.setActive(true);

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(userHabitService.acceptUserHabit("user123", "habit456")).thenReturn(userHabit);

        mockMvc.perform(post("/api/my-habits/habit456")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uh123"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.habitId").value("habit456"))
                .andExpect(jsonPath("$.habitName").value("Read for 20 minutes"))
                .andExpect(jsonPath("$.currentStreak").value(0))
                .andExpect(jsonPath("$.active").value(true));

        verify(userHabitService).acceptUserHabit("user123", "habit456");
    }

    @Test
    void acceptUserHabit_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/my-habits/habit456"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUserHabit_whenAuthenticated_returnsNoContent() throws Exception {
        AppUser mockUser = createMockUser();

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);

        mockMvc.perform(delete("/api/my-habits/habit456")
                        .with(oidcLogin()))
                .andExpect(status().isNoContent());

        verify(userHabitService).deleteUserHabit("user123", "habit456");
    }

    @Test
    void deleteUserHabit_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/my-habits/habit456"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void completeUserHabit_whenAuthenticated_returnsCompletedHabit() throws Exception {
        AppUser mockUser = createMockUser();

        UserHabit completedHabit = new UserHabit();
        completedHabit.setId("uh123");
        completedHabit.setUserId("user123");
        completedHabit.setHabitId("habit456");
        completedHabit.setHabitName("Read for 20 minutes");
        completedHabit.setDifficulty(HabitDifficulty.MEDIUM);
        completedHabit.setFrequency(HabitFrequency.DAILY);
        completedHabit.setCurrentStreak(5);
        completedHabit.setLongestStreak(5);
        completedHabit.setTotalCompletions(10);
        completedHabit.setActive(true);

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(userHabitService.completeUserHabit("user123", "habit456")).thenReturn(completedHabit);
        when(appUserService.addXp("user123", HabitDifficulty.MEDIUM.getBaseXp())).thenReturn(mockUser);

        mockMvc.perform(post("/api/my-habits/habit456/complete")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uh123"))
                .andExpect(jsonPath("$.habitName").value("Read for 20 minutes"))
                .andExpect(jsonPath("$.currentStreak").value(5))
                .andExpect(jsonPath("$.totalCompletions").value(10));

        verify(userHabitService).completeUserHabit("user123", "habit456");
        verify(appUserService).addXp("user123", HabitDifficulty.MEDIUM.getBaseXp());
    }

    @Test
    void completeUserHabit_whenNotAuthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/my-habits/habit456/complete"))
                .andExpect(status().isUnauthorized());
    }
}