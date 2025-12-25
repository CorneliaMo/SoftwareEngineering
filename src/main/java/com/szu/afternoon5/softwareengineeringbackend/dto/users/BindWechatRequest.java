package com.szu.afternoon5.softwareengineeringbackend.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 绑定微信请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BindWechatRequest {

    /**
     * 微信登录临时凭证
     */
    @NotBlank
    @JsonProperty("jscode")
    private String jscode;
}
