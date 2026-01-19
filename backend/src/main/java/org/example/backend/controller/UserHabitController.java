package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.AppUser;
import org.example.backend.model.UserHabit;
import org.example.backend.service.AppUserService;
import org.example.backend.service.UserHabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-habits")
public class UserHabitController {

    private final  UserHabitService userHabitService;
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<UserHabit>> getMyHabits(@AuthenticationPrincipal OAuth2User oAuth2User) {
        AppUser user = appUserService.getOrCreateUser(oAuth2User);
        return ResponseEntity.ok(userHabitService.getUserHabits(user.getId()));
    }

    @PostMapping("/{habitId}")
    public ResponseEntity<UserHabit> acceptUserHabit(
           @AuthenticationPrincipal OAuth2User oAuth2User,
           @PathVariable String habitId) {
        AppUser user = appUserService.getOrCreateUser(oAuth2User);
        return ResponseEntity.ok(userHabitService.acceptUserHabit(user.getId(), habitId));
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteUserHabit(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable String habitId) {

        AppUser user = appUserService.getOrCreateUser(oAuth2User);
        userHabitService.deleteUserHabit(user.getId(), habitId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<UserHabit> completeUserHabit(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable String habitId) {

        AppUser user = appUserService.getOrCreateUser(oAuth2User);
        UserHabit completedHabit = userHabitService.completeUserHabit(user.getId(), habitId);
        appUserService.addXp(user.getId(), completedHabit.getDifficulty().getBaseXp());

        return ResponseEntity.ok(completedHabit);
    }
}
