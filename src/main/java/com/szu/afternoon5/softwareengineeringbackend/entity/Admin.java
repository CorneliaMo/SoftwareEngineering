package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@Table(schema = "admins")
public class Admin {

    @Id
    private Long adminId;

    private Long userId;

    private String username;

    private String password;

    private String adminName;

    private String role;

    private Boolean status;

    private Instant lastLogin;

    private Instant createdTime;

    public Admin(Long userId, String username, String password, String adminName, String role) {
        this.adminId = null;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.adminName = adminName;
        this.role = role;
        this.status = Boolean.TRUE;
        this.lastLogin = null;
        this.createdTime = Instant.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
