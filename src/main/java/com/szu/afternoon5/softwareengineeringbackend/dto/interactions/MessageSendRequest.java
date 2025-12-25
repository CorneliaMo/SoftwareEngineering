package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发送消息请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MessageSendRequest {

    @NotBlank
    @JsonProperty("text")
    private String text;
}
