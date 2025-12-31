package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.MessageInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.Message;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.MessageInfo(m.messageId, m.senderId, m.msgType, m.content, m.createdTime) FROM Message m
    WHERE m.conversationId = :conversationId
    AND (:afterId IS NULL OR m.messageId >= :afterId)
    AND (:beforeId IS NULL OR m.messageId <= :beforeId)
    AND (m.isRecalled = false)
""")
    /*
      获取会话消息列表（分页版本）。
     */
    List<MessageInfo> getConversationMessages(Long conversationId, Long beforeId, Long afterId, PageRequest pageRequest);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.MessageInfo(m.messageId, m.senderId, m.msgType, m.content, m.createdTime) FROM Message m
    WHERE m.conversationId = :conversationId
    AND (:afterId IS NULL OR m.messageId >= :afterId)
    AND (:beforeId IS NULL OR m.messageId <= :beforeId)
    AND (m.isRecalled = false)
""")
    /*
      获取会话消息列表（排序版本）。
     */
    List<MessageInfo> getConversationMessages(Long conversationId, Long beforeId, Long afterId, Sort sort);
}
