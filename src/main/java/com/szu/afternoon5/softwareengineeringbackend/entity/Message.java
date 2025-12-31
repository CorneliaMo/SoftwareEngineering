package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.Instant;

/**
 * 消息实体
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long conversationId;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private MessageType msgType;

    private String content;

    private Instant createdTime;

    private Boolean isRecalled;

    @Nullable
    private Instant recalledTime;

    public enum MessageType {
        text
    }

    public Message(Long conversationId, Long senderId, MessageType msgType, String content) {
        this.messageId = null;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.msgType = msgType;
        this.content = content;
        this.createdTime = Instant.now();
        this.isRecalled = false;
        this.recalledTime = null;
    }
}
