package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public AppUser(String githubId, String username, String email, String avatarUrl) {
        this.githubId = githubId;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}
