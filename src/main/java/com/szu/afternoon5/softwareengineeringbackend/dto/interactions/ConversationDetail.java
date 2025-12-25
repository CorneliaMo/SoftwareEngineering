package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Conversation;
import com.szu.afternoon5.softwareengineeringbackend.entity.ConversationOneToOneMap;
import com.szu.afternoon5.softwareengineeringbackend.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 会话详情
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
public class ConversationDetail {

    @JsonProperty("conversation_id")
    private Long conversationId;

    @JsonProperty("created_time")
    private Instant createdTime;

    @JsonProperty("updated_time")
    private Instant updatedTime;

    @JsonProperty("last_message_id")
    private Long lastMessageId;

    @JsonProperty("last_message_time")
    private Instant lastMessageTime;

    @JsonProperty("last_read_message_id")
    private Long lastReadMessageId;

    @JsonProperty("last_read_time")
    private Instant lastReadTime;

    @JsonProperty("is_hidden")
    private Boolean isHidden;

    @JsonProperty("hidden_time")
    private Instant hiddenTime;

    @JsonProperty("user_low_id")
    private Long userLowId;

    @JsonProperty("user_high_id")
    private Long userHighId;

    public ConversationDetail(Conversation conversation, Participant participant, ConversationOneToOneMap conversationOneToOneMap) {
        this.conversationId = conversation.getConversationId();
        this.createdTime = conversation.getCreatedTime();
        this.updatedTime = conversation.getUpdatedTime();
        this.lastMessageId = conversation.getLastMessageId();
        this.lastMessageTime = conversation.getLastMessageTime();
        this.lastReadMessageId = participant.getLastReadMessageId();
        this.lastReadTime = participant.getLastReadTime();
        this.isHidden = participant.getIsHidden();
        this.hiddenTime = participant.getHiddenTime();
        this.userLowId = conversationOneToOneMap.getUserLowId();
        this.userHighId = conversationOneToOneMap.getUserHighId();
    }

}
