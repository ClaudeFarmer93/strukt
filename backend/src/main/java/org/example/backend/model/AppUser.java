package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class AppUser {

    @Id
    private String id;
    private String githubId;
    private String username;
    private String email;
    private String avatarUrl;

    private int totalXp;
    private int level;

    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;
    public AppUser(String githubId, String username, String email, String avatarUrl) {
        this.githubId = githubId;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.totalXp = 0;
        this.level = 0;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.lastActiveDate = null;
    }

    public int getXpForNextLevel() {
        return  level * 100;
    }

    public int getCurrentLevelXp() {
        int xpUsed = 0;
        for(int i = 1; i<level; i++) {
            xpUsed += i * 100;
        }
        return totalXp - xpUsed;
    }
}
