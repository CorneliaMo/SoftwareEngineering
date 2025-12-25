package com.szu.afternoon5.softwareengineeringbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class ConversationOneToOneMapId implements Serializable {

    private Long userLowId;

    private Long userHighId;

    private Long conversationId;
}
