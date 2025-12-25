package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    private Long conversationId;

    private Long userId;

    @Nullable
    private Long lastReadMessageId;

    @Nullable
    private Instant lastReadTime;

    private Boolean isHidden;

    @Nullable
    private Instant hiddenTime;

    private Instant createdTime;

    private Instant updatedTime;

    public Participant(Long conversationId, Long userId) {
        this.recordId = null;
        this.conversationId = conversationId;
        this.userId = userId;
        this.lastReadMessageId = null;
        this.lastReadTime = null;
        this.isHidden = false;
        this.hiddenTime = null;
        this.createdTime = Instant.now();
        this.updatedTime = Instant.now();
    }
}
