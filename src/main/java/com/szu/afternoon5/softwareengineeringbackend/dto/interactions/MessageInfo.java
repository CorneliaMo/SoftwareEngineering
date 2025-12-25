package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 消息摘要信息
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
public class MessageInfo {

    @JsonProperty("message_id")
    private Long messageId;

    @JsonProperty("sender_id")
    private Long senderId;

    @JsonProperty("msg_type")
    private Message.MessageType msgType;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_time")
    private Instant createdTime;

    public MessageInfo(Message message) {
        this.messageId = message.getMessageId();
        this.senderId = message.getSenderId();
        this.msgType = message.getMsgType();
        this.content = message.getContent();
        this.createdTime = message.getCreatedTime();
    }
}
