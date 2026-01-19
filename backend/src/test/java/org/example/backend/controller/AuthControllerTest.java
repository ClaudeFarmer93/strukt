package org.example.backend.controller;

import org.example.backend.model.AppUser;
import org.example.backend.repository.AppUserRepository;
import org.example.backend.security.SecurityConfig;
import org.example.backend.service.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void getMe_whenAuthenticated_returnsUser() throws Exception {
        AppUser mockUser = new AppUser("12345", "testUser", "test@example.com", "https://example.com/avatar.png");
        mockUser.setId("abc123");

        when(appUserService.getOrCreateUser(any(OAuth2User.class))).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/me")
                        .with(oidcLogin().userInfoToken(token -> token
                                .claim("id", 12345)
                                .claim("login", "testUser")
                                .claim("email", "test@example.com")
                                .claim("avatar_url", "https://example.com/avatar.png")
                        ))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("""
                        {
                            "id": "abc123",
                            "githubId": "12345",
                            "username": "testUser",
                            "email": "test@example.com",
                            "avatarUrl": "https://example.com/avatar.png",
                            "totalXp": 0,
                            "level": 1,
                            "currentStreak": 0,
                            "longestStreak": 0,
                            "lastActiveDate": null
                        }
                        """));
    }

    @Test
    void getMe_whenNotAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/me"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}