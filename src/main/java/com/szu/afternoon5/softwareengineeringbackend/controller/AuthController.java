package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.auth.*;
import com.szu.afternoon5.softwareengineeringbackend.service.SecurityService;
import com.szu.afternoon5.softwareengineeringbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口控制器，预留登录、注销、令牌刷新等入口。
 * <p>
 * 后续可在此定义微信登录、短信登录等具体接口，并结合 {@code JwtAuthFilter} 返回统一的认证结果。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SecurityService securityService;

    /**
     * 构建认证控制器并注入认证依赖。
     */
    public AuthController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * 微信登录接口。
     *
     * @param request 微信登录请求体
     * @return 登录结果，包含用户信息与 JWT
     */
    @PostMapping("/wechat-login")
    public UserAuthResponse wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return userService.wechatLogin(request);
    }

    /**
     * 刷新令牌
     * @param request 刷新令牌请求体
     * @return 新的访问令牌，如果刷新令牌即将过期，也会返回新的刷新令牌
     */
    @PostMapping("/refresh-token")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return securityService.refreshAccessToken(request);
    }

    /**
     * 用户账密注册。
     *
     * @param request 注册请求体
     * @return 注册结果，包含用户信息与签发的 JWT
     */
    @PostMapping("/register")
    public UserAuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 用户账密登录。
     *
     * @param request 登录请求体
     * @return 登录结果，包含用户信息与签发的 JWT
     */
    @PostMapping("/login")
    public UserAuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.userLogin(request);
    }

    /**
     * 用户修改密码。
     *
     * @param request 密码修改请求体
     */
    @PutMapping("/reset-password")
    @PreAuthorize("isAuthenticated()")
    public ResetPasswordResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request, Authentication authentication) {
        return userService.resetPassword(request, authentication);
    }

    /**
     * 管理员账密登录。
     *
     * @param request 管理员登录请求体
     * @return 登录结果，包含管理员信息与签发的 JWT
     */
    @PostMapping("/admin-login")
    public AdminAuthResponse adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        return userService.adminLogin(request);
    }
}
