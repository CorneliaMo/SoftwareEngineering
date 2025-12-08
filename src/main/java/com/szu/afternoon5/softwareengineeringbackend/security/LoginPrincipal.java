package com.szu.afternoon5.softwareengineeringbackend.security;

import lombok.Data;

/**
 * 安全上下文中的登录主体，封装用户或管理员身份信息。
 * <p>
 * 后续若引入角色列表或权限集合，可在此拓展字段并实现 {@code UserDetails} 以便与 Spring Security 深度集成。
 */
@Data
public class LoginPrincipal {

    /**
     * 普通用户 ID，管理员登录时可为空。
     */
    private Long userId;

    /**
     * 管理员 ID，普通用户登录时可为空。
     */
    private Long adminId;

    private LoginType loginType;

    public enum LoginType {
        admin, user
    }

    public LoginPrincipal(Long userId, Long adminId, LoginType loginType) {
        this.userId = userId;
        this.adminId = adminId;
        this.loginType = loginType;
    }
}
