package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * 用户注销请求实体
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_deletion_requests",
       uniqueConstraints = @UniqueConstraint(name = "uq_user_deletion_requests_user", columnNames = "user_id"),
       indexes = @Index(name = "idx_user_deletion_requests_due", columnList = "status, execute_after"))
public class UserDeletionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "requested_time", nullable = false)
    private Instant requestedTime;

    @Column(name = "execute_after", nullable = false)
    private Instant executeAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeletionRequestStatus status;

    @Column(name = "processed_time")
    private Instant processedTime;

    @Column(name = "fail_reason")
    private String failReason;

    public enum DeletionRequestStatus {
        PENDING,
        PROCESSING,
        DONE,
        CANCELLED,
        FAILED
    }

    public UserDeletionRequest(Long userId, Instant executeAfter) {
        this.requestId = null;
        this.userId = userId;
        this.requestedTime = Instant.now();
        this.executeAfter = executeAfter;
        this.status = DeletionRequestStatus.PENDING;
        this.processedTime = null;
        this.failReason = null;
    }
}
