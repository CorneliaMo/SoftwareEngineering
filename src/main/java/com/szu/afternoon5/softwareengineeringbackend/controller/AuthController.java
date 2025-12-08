package com.szu.afternoon5.softwareengineeringbackend.controller;

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
}
