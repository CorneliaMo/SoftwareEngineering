package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserDeletionRequestRepository extends JpaRepository<UserDeletionRequest, Long> {

    /**
     * 根据用户ID查询注销申请。
     */
    Optional<UserDeletionRequest> findByUserId(Long userId);

    /**
     * 查询需要执行的注销请求列表。
     */
    @Query("""
        SELECT r FROM UserDeletionRequest r
        WHERE r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PENDING
          AND r.executeAfter <= :now
        ORDER BY r.executeAfter ASC
    """)
    List<UserDeletionRequest> findDueRequests(@Param("now") Instant now);

    /**
     * 认领一条待处理的注销请求，防止并发重复处理。
     */
    @Modifying
    @Query("""
        UPDATE UserDeletionRequest r
        SET r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PROCESSING
        WHERE r.requestId = :requestId
          AND r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PENDING
    """)
    int claim(@Param("requestId") Long requestId);

    /**
     * 更新注销请求处理结果。
     */
    @Modifying
    @Query("""
        UPDATE UserDeletionRequest r
        SET r.status = :status, r.processedTime = :processedAt, r.failReason = :failReason
        WHERE r.requestId = :requestId
    """)
    int markResult(@Param("requestId") Long requestId,
                   @Param("status") UserDeletionRequest.DeletionRequestStatus status,
                   @Param("processedAt") Instant processedAt,
                   @Param("failReason") String failReason);

    /**
     * 判断用户或openid是否存在待处理的注销申请。
     */
    @Query("""
    SELECT (COUNT(u) > 0) FROM UserDeletionRequest r
    JOIN User u ON r.userId = u.userId
    WHERE (:userId IS NOT NULL AND r.userId = :userId)
        OR (:openid IS NOT NULL AND u.openid = :openid)
""")
    boolean existsByUserIdOrOpenid(Long userId, String openid);
}
