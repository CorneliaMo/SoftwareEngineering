package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 会话消息列表响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConversationMessageListResponse extends BaseResponse {

    @JsonProperty("messages")
    private List<MessageInfo> messages;

    public ConversationMessageListResponse(List<MessageInfo> messages) {
        super();
        this.messages = messages;
    }
}
