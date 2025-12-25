package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 会话列表响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConversationListResponse extends BaseResponse {

    @JsonProperty("conversations")
    private List<ConversationDetail> conversations;

    public ConversationListResponse(List<ConversationDetail> conversations) {
        super();
        this.conversations = conversations;
    }
}
