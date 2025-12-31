package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求体
 */
@Data
public class WechatLoginRequest {

    @NotBlank
    private String jscode;

}
