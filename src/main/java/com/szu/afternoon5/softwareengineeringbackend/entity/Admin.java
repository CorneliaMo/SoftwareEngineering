package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.Instant;

/**
 * 管理员实体，对应后台账号及权限信息。
 * <p>
 * 后续可为管理员增加权限集合、登录审计字段，并结合 ORM 注解完善约束（如唯一索引、非空限制）。
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Nullable
    private Long userId;

    private String username;

    private String password;

    @Nullable
    private String adminName;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    private Boolean status;

    @Nullable
    private Instant lastLogin;

    private Instant createdTime;

    /**
     * 默认构造方法，初始化基础字段并将状态设为启用。
     */
    public Admin(Long userId, String username, String password, String adminName, AdminRole role) {
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

    /**
     * 在持久化前填充创建时间，保证时间字段完整。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }

    public enum AdminRole {
        admin, superadmin
    }
}
