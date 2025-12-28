package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 新增管理员请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminCreateRequest {

    /**
     * 管理员用户名
     */
    @NotBlank
    @JsonProperty("username")
    private String username;

    /**
     * 管理员密码
     */
    @NotBlank
    @JsonProperty("password")
    private String password;

}
