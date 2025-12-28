package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatLoginRequest {

    @NotBlank
    private String jscode;

}
