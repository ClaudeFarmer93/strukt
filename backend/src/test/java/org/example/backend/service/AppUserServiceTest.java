package org.example.backend.service;

import org.example.backend.model.AppUser;
import org.example.backend.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final AppUserService appUserService = new AppUserService(appUserRepository);

    @Test
    void getUserById_returnsUser() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));

        AppUser result = appUserService.getUserById("user123");

        assertNotNull(result);
        assertEquals("user123", result.getId());
        assertEquals("testUser", result.getUsername());
        verify(appUserRepository).findById("user123");
    }

    @Test
    void getUserById_whenNotFound_throwsException() {
        when(appUserRepository.findById("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> appUserService.getUserById("nonexistent"));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getOrCreateUser_existingUser_returnsUser() {
        AppUser existingUser = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        existingUser.setId("user123");

        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("id")).thenReturn(123);
        when(oAuth2User.getAttribute("login")).thenReturn("testUser");
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oAuth2User.getAttribute("avatar_url")).thenReturn("https://example.com/avatar.png");

        when(appUserRepository.findByGithubId("123")).thenReturn(Optional.of(existingUser));

        AppUser result = appUserService.getOrCreateUser(oAuth2User);

        assertNotNull(result);
        assertEquals("user123", result.getId());
        verify(appUserRepository).findByGithubId("123");
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void getOrCreateUser_newUser_createsAndReturnsUser() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("id")).thenReturn(456);
        when(oAuth2User.getAttribute("login")).thenReturn("newUser");
        when(oAuth2User.getAttribute("email")).thenReturn("new@example.com");
        when(oAuth2User.getAttribute("avatar_url")).thenReturn("https://example.com/new-avatar.png");

        when(appUserRepository.findByGithubId("456")).thenReturn(Optional.empty());
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser saved = invocation.getArgument(0);
            saved.setId("newUser123");
            return saved;
        });

        AppUser result = appUserService.getOrCreateUser(oAuth2User);

        assertNotNull(result);
        assertEquals("newUser123", result.getId());
        assertEquals("newUser", result.getUsername());
        assertEquals(1, result.getLevel());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void addXp_addsXpToUser() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(50);
        user.setLevel(1);
        user.setCurrentStreak(0);
        user.setLongestStreak(0);
        user.setLastActiveDate(null);

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 25);

        assertEquals(75, result.getTotalXp());
        assertEquals(1, result.getCurrentStreak());
        assertEquals(1, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
        verify(appUserRepository).save(user);
    }

    @Test
    void addXp_levelsUpUser() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(90);
        user.setLevel(1);
        user.setCurrentStreak(1);
        user.setLongestStreak(1);
        user.setLastActiveDate(LocalDate.now().minusDays(1));

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 50);

        assertEquals(140, result.getTotalXp());
        assertEquals(2, result.getLevel());
        assertEquals(2, result.getCurrentStreak());
        assertEquals(2, result.getLongestStreak());
    }

    @Test
    void addXp_consecutiveDay_incrementsStreak() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(50);
        user.setLevel(1);
        user.setCurrentStreak(5);
        user.setLongestStreak(5);
        user.setLastActiveDate(LocalDate.now().minusDays(1));

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 10);

        assertEquals(6, result.getCurrentStreak());
        assertEquals(6, result.getLongestStreak());
    }

    @Test
    void addXp_missedDay_resetsStreak() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(50);
        user.setLevel(1);
        user.setCurrentStreak(5);
        user.setLongestStreak(10);
        user.setLastActiveDate(LocalDate.now().minusDays(3));

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 10);

        assertEquals(1, result.getCurrentStreak());
        assertEquals(10, result.getLongestStreak());
    }

    @Test
    void addXp_sameDay_keepsStreakUnchanged() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(50);
        user.setLevel(1);
        user.setCurrentStreak(5);
        user.setLongestStreak(5);
        user.setLastActiveDate(LocalDate.now());

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 10);

        assertEquals(60, result.getTotalXp());
        assertEquals(5, result.getCurrentStreak());
        assertEquals(5, result.getLongestStreak());
    }

    @Test
    void addXp_firstTimeActivity_startsStreakAtOne() {
        AppUser user = new AppUser("github123", "testUser", "test@example.com", "https://example.com/avatar.png");
        user.setId("user123");
        user.setTotalXp(0);
        user.setLevel(1);
        user.setCurrentStreak(0);
        user.setLongestStreak(0);
        user.setLastActiveDate(null);

        when(appUserRepository.findById("user123")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.addXp("user123", 25);

        assertEquals(1, result.getCurrentStreak());
        assertEquals(1, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());
    }
}