package com.szu.afternoon5.softwareengineeringbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 一对一会话映射复合主键
 */
@NoArgsConstructor
@EqualsAndHashCode
public class ConversationOneToOneMapId implements Serializable {

    private Long userLowId;

    private Long userHighId;

    private Long conversationId;
}
