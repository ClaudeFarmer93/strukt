package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.AppUser;
import org.example.backend.model.HabitCompletion;
import org.example.backend.service.AppUserService;
import org.example.backend.service.HabitCompletionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/completions")
@RequiredArgsConstructor
public class HabitCompletionController {

    private final HabitCompletionService habitCompletionService;
    private final AppUserService appUserService;

    @GetMapping("/week")
    public ResponseEntity<List<HabitCompletion>> getWeekCompletions(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            ) {

        AppUser user = appUserService.getOrCreateUser(oAuth2User);
        LocalDate targetDate = date != null ? date : LocalDate.now();

        return ResponseEntity.ok(
                habitCompletionService.getCompletionsForWeek(user.getId(), targetDate)
        );
    }

}
