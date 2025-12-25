package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建会话请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ConversationCreateRequest {

    @NotNull
    @JsonProperty("user_id")
    private Long userId;
}
