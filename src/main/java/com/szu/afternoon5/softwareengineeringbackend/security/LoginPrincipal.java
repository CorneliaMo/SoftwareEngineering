package com.szu.afternoon5.softwareengineeringbackend.security;

import lombok.Data;

@Data
public class LoginPrincipal {

    private Long userId;

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
