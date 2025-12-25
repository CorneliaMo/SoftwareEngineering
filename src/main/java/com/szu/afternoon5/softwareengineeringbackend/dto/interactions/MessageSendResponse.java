package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发送消息响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MessageSendResponse extends BaseResponse {

    @JsonProperty("message")
    private MessageInfo message;

    public MessageSendResponse(MessageInfo message) {
        super();
        this.message = message;
    }
}
