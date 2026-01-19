package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.AppUser;
import org.example.backend.repository.AppUserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUser getOrCreateUser(OAuth2User oAuth2User) {
        String githubId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        String username = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        return appUserRepository.findByGithubId(githubId)
                .orElseGet(() -> {
                    AppUser appUser = new AppUser(githubId, username, email, avatarUrl);
                   return appUserRepository.save(appUser);
                });
    }

    public AppUser getUserById(String userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    public AppUser addXp(String userId, int xpAdded) {
        AppUser appUser = getUserById(userId);

        appUser.setTotalXp(appUser.getTotalXp() + xpAdded);

        while (appUser.getTotalXp() >= getXpRequiredForNextLevel(appUser.getLevel() +1)) {
            appUser.setLevel(appUser.getLevel() + 1);
        }
        LocalDate today = LocalDate.now();
        LocalDate lastActive = appUser.getLastActiveDate();

        if (lastActive == null || lastActive.isBefore(today.minusDays(1))) {
            appUser.setCurrentStreak(1);
        } else if (lastActive.isBefore(today)) {
            appUser.setCurrentStreak(appUser.getCurrentStreak() + 1);
        }
        if (appUser.getCurrentStreak() > appUser.getLongestStreak()) {
            appUser.setLongestStreak(appUser.getCurrentStreak());
        }
        appUser.setLastActiveDate(today);

        return appUserRepository.save(appUser);
    }

    private int getXpRequiredForNextLevel(int level) {
        int total = 0;
        for(int i = 1; i < level; i++) {
            total += i*100;

        }
        return total;
    }
}
