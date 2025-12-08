package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * 用户实体，记录基础账号信息与互动统计。
 * <p>
 * 后续可增加账户安全字段（如盐值、多因素状态）、偏好设置或第三方绑定标记，并完善索引以提升查询性能。
 */
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

    /**
     * 创建用户的构造器，初始化启用状态与计数字段。
     */
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

    /**
     * 在保存前自动填充创建时间，避免空值导致的排序问题。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
