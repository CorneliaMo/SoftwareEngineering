package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建会话响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConversationCreateResponse extends BaseResponse {

    @JsonProperty("conversation")
    private ConversationDetail conversation;

    public ConversationCreateResponse(ConversationDetail conversation) {
        super();
        this.conversation = conversation;
    }
}
