package org.example.backend.controller;

import org.example.backend.model.AppUser;
import org.example.backend.model.HabitCompletion;
import org.example.backend.model.HabitDifficulty;
import org.example.backend.model.HabitFrequency;
import org.example.backend.repository.AppUserRepository;
import org.example.backend.security.SecurityConfig;
import org.example.backend.service.AppUserService;
import org.example.backend.service.HabitCompletionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitCompletionController.class)
@Import(SecurityConfig.class)
public class HabitCompletionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitCompletionService habitCompletionService;

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    private AppUser createMockUser() {
        AppUser user = new AppUser("12345", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        return user;
    }

    private HabitCompletion createMockCompletion(String id, String habitName, String completionDate) {
        HabitCompletion completion = new HabitCompletion();
        completion.setId(id);
        completion.setUserId("user123");
        completion.setUserHabitId("uh123");
        completion.setHabitId("habit123");
        completion.setHabitName(habitName);
        completion.setDifficulty(HabitDifficulty.EASY);
        completion.setFrequency(HabitFrequency.DAILY);
        completion.setCompletionDate(completionDate);
        completion.setXpEarned(25);
        return completion;
    }

    @Test
    void getWeekCompletions_whenAuthenticated_returnsCompletions() throws Exception {
        AppUser mockUser = createMockUser();

        HabitCompletion completion1 = createMockCompletion("c1", "Make your bed", "2026-01-27");
        HabitCompletion completion2 = createMockCompletion("c2", "Take vitamins", "2026-01-28");

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(habitCompletionService.getCompletionsForWeek(eq("user123"), any(LocalDate.class)))
                .thenReturn(List.of(completion1, completion2));

        mockMvc.perform(get("/api/completions/week")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].habitName").value("Make your bed"))
                .andExpect(jsonPath("$[0].completionDate").value("2026-01-27"))
                .andExpect(jsonPath("$[0].xpEarned").value(25))
                .andExpect(jsonPath("$[1].id").value("c2"))
                .andExpect(jsonPath("$[1].habitName").value("Take vitamins"));
    }

    @Test
    void getWeekCompletions_whenAuthenticatedWithDateParam_usesProvidedDate() throws Exception {
        AppUser mockUser = createMockUser();

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(habitCompletionService.getCompletionsForWeek("user123", LocalDate.of(2026, 1, 20)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/completions/week")
                        .param("date", "2026-01-20")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(habitCompletionService).getCompletionsForWeek("user123", LocalDate.of(2026, 1, 20));
    }

    @Test
    void getWeekCompletions_whenAuthenticatedWithoutDateParam_usesToday() throws Exception {
        AppUser mockUser = createMockUser();
        LocalDate today = LocalDate.now();

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(habitCompletionService.getCompletionsForWeek("user123", today))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/completions/week")
                        .with(oidcLogin()))
                .andExpect(status().isOk());

        verify(habitCompletionService).getCompletionsForWeek("user123", today);
    }

    @Test
    void getWeekCompletions_whenNotAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/completions/week"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getWeekCompletions_whenNoCompletions_returnsEmptyList() throws Exception {
        AppUser mockUser = createMockUser();

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(habitCompletionService.getCompletionsForWeek(eq("user123"), any(LocalDate.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/completions/week")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getWeekCompletions_returnsCorrectCompletionFields() throws Exception {
        AppUser mockUser = createMockUser();

        HabitCompletion completion = new HabitCompletion();
        completion.setId("c1");
        completion.setUserId("user123");
        completion.setUserHabitId("uh456");
        completion.setHabitId("habit789");
        completion.setHabitName("Meditate for 10 minutes");
        completion.setDifficulty(HabitDifficulty.MEDIUM);
        completion.setFrequency(HabitFrequency.DAILY);
        completion.setCompletionDate("2026-01-28");
        completion.setXpEarned(50);

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);
        when(habitCompletionService.getCompletionsForWeek(eq("user123"), any(LocalDate.class)))
                .thenReturn(List.of(completion));

        mockMvc.perform(get("/api/completions/week")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].userHabitId").value("uh456"))
                .andExpect(jsonPath("$[0].habitId").value("habit789"))
                .andExpect(jsonPath("$[0].habitName").value("Meditate for 10 minutes"))
                .andExpect(jsonPath("$[0].difficulty").value("MEDIUM"))
                .andExpect(jsonPath("$[0].frequency").value("DAILY"))
                .andExpect(jsonPath("$[0].completionDate").value("2026-01-28"))
                .andExpect(jsonPath("$[0].xpEarned").value(50));
    }
}