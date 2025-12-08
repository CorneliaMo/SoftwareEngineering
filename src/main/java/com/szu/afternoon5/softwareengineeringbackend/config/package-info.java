/**
 * 后端服务的 Spring 配置组件所在包。
 * <p>
 * 通过 {@link com.szu.afternoon5.softwareengineeringbackend.config.SecurityConfig} 配置安全过滤链和 CORS 规则，
 * 使用 {@link com.szu.afternoon5.softwareengineeringbackend.config.JwtConfig} 绑定 JWT 相关属性，
 * 并在 {@link com.szu.afternoon5.softwareengineeringbackend.config.WechatConfig} 与
 * {@link com.szu.afternoon5.softwareengineeringbackend.config.WechatClientConfig} 中初始化微信小程序所需的参数与客户端。
 */
package com.szu.afternoon5.softwareengineeringbackend.config;

