package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.ConversationDetail;
import com.szu.afternoon5.softwareengineeringbackend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.ConversationDetail(cv.conversationId, cv.createdTime, cv.updatedTime, cv.lastMessageId, cv.lastMessageTime, p.lastReadMessageId, p.lastReadTime, p.isHidden, p.hiddenTime, cvm.userLowId, cvm.userHighId) FROM Participant p
    JOIN Conversation cv ON p.conversationId = cv.conversationId
    JOIN ConversationOneToOneMap cvm ON p.conversationId = cvm.conversationId
    WHERE p.userId = :userId
""")
    /*
      获取指定用户的会话列表。
     */
    List<ConversationDetail> getConversations(Long userId);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.ConversationDetail(
      cv.conversationId, cv.createdTime, cv.updatedTime, cv.lastMessageId, cv.lastMessageTime,
      p.lastReadMessageId, p.lastReadTime, p.isHidden, p.hiddenTime,
      cvm.userLowId, cvm.userHighId
    )
    FROM ConversationOneToOneMap cvm
    JOIN Conversation cv ON cv.conversationId = cvm.conversationId
    JOIN Participant p ON p.conversationId = cvm.conversationId AND p.userId = :me
    WHERE (cvm.userLowId = :me AND cvm.userHighId = :other)
       OR (cvm.userLowId = :other AND cvm.userHighId = :me)
""")
    /*
      获取指定两位用户之间的会话详情。
     */
    Optional<ConversationDetail> getConversation(Long me, Long other);

    @Query("""
    SELECT (COUNT(cvm) > 0) FROM Conversation c
    JOIN ConversationOneToOneMap cvm ON c.conversationId = cvm.conversationId
    WHERE c.conversationId = :conversationId AND (cvm.userLowId = :participantId OR cvm.userHighId = :participantId)
""")
    /*
      判断指定用户是否为会话参与者。
     */
    boolean existsByConversationIdAndParticipantId(Long conversationId, Long participantId);
}
