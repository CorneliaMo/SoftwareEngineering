package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Table(schema = "users")
public class User {

    @Id
    private Long userId;

    private String username;

    private String password;

    private String email;

    private String nickname;

    private String avatarUrl;

    private Boolean status;

    private Instant createdTime;

    private Instant updatedTime;

    private Integer commentCount;

    private Integer postCount;

    private Integer ratingCount;

    public User(String username, String password, String email, String nickname, String avatarUrl) {
        this.userId = null;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.status = Boolean.TRUE;
        this.createdTime = Instant.now();
        this.updatedTime = Instant.now();
        this.commentCount = 0;
        this.postCount = 0;
        this.ratingCount = 0;
    }

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
