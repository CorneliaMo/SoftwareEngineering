package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@IdClass(ConversationOneToOneMapId.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conversation_one_to_one_map")
public class ConversationOneToOneMap {

    @Id
    private Long userLowId;

    @Id
    private Long userHighId;

    @Id
    private Long conversationId;

    private Instant createdTime;

    public ConversationOneToOneMap(Long userLowId, Long userHighId, Long conversationId) {
        this.userLowId = userLowId;
        this.userHighId = userHighId;
        this.conversationId = conversationId;
        this.createdTime = Instant.now();
    }
}
