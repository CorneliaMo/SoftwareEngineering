package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserDeletionRequestRepository extends JpaRepository<UserDeletionRequest, Long> {

    Optional<UserDeletionRequest> findByUserId(Long userId);

    @Query("""
        SELECT r FROM UserDeletionRequest r
        WHERE r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PENDING
          AND r.executeAfter <= :now
        ORDER BY r.executeAfter ASC
    """)
    List<UserDeletionRequest> findDueRequests(@Param("now") Instant now);

    @Modifying
    @Query("""
        UPDATE UserDeletionRequest r
        SET r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PROCESSING
        WHERE r.requestId = :requestId
          AND r.status = com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest.DeletionRequestStatus.PENDING
    """)
    int claim(@Param("requestId") Long requestId);

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
}
