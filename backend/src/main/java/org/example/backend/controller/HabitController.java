package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.AppUser;
import org.example.backend.model.Habit;
import org.example.backend.model.UserHabit;
import org.example.backend.service.AppUserService;
import org.example.backend.service.HabitService;
import org.example.backend.service.UserHabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final UserHabitService userHabitService;
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }


    @GetMapping("/daily")
    public ResponseEntity<Habit> getRandomDailyHabit(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if(oAuth2User != null) {
            AppUser user = appUserService.getOrCreateUser(oAuth2User);
            List<String> userHabitIds = userHabitService.getUserHabits((user.getId()))
                    .stream()
                    .map(UserHabit::getHabitId)
                    .toList();
        return ResponseEntity.ok(habitService.getRandomDailyHabitExcluding(userHabitIds));
        }
        return ResponseEntity.ok(habitService.getRandomDailyHabit());
    }

    @GetMapping("/weekly")
    public ResponseEntity<Habit> getRandomWeeklyHabit(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if(oAuth2User != null) {
            AppUser user = appUserService.getOrCreateUser(oAuth2User);
            List<String> userHabitIds = userHabitService.getUserHabits((user.getId()))
                    .stream()
                    .map(UserHabit::getHabitId)
                    .toList();
            return ResponseEntity.ok(habitService.getRandomWeeklyHabitExcluding(userHabitIds));
        }
        return ResponseEntity.ok(habitService.getRandomWeeklyHabit());
    }
}
