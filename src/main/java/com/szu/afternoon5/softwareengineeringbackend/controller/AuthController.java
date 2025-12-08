package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.AdminAuthResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.AdminLoginRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.LoginRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.RegisterRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.ResetPasswordRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserAuthResponse;
import jakarta.validation.Valid;
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

    /**
     * 用户账密注册。
     *
     * @param request 注册请求体
     * @return 注册结果，包含用户信息与签发的 JWT
     */
    @PostMapping("/register")
    public UserAuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return null;
    }

    /**
     * 用户账密登录。
     *
     * @param request 登录请求体
     * @return 登录结果，包含用户信息与签发的 JWT
     */
    @PostMapping("/login")
    public UserAuthResponse login(@Valid @RequestBody LoginRequest request) {
        return null;
    }

    /**
     * 用户修改密码。
     *
     * @param request 密码修改请求体
     */
    @PutMapping("/reset-password")
    public BaseResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return null;
    }

    /**
     * 管理员账密登录。
     *
     * @param request 管理员登录请求体
     * @return 登录结果，包含管理员信息与签发的 JWT
     */
    @PostMapping("/admin-login")
    public AdminAuthResponse adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        return null;
    }
}
