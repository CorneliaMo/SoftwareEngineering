package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.Instant;

/**
 * 会话实体
 */
@Data
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    private Instant createdTime;

    private Instant updatedTime;

    @Nullable
    private Long lastMessageId;

    @Nullable
    private Instant lastMessageTime;

    public Conversation() {
        conversationId = null;
        createdTime = Instant.now();
        updatedTime = Instant.now();
        lastMessageId = null;
        lastMessageTime = null;
    }
}
