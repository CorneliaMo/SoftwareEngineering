package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Table(schema = "operation_logs")
public class OperationLog {

    @Id
    private Long logId;

    private Long adminId;

    private String operationType;

    private Long targetId;

    private String targetType;

    private String operationDetail;

    private String ipAddress;

    private Instant createdTime;

    public OperationLog(Long adminId, String operationType, Long targetId, String targetType, String operationDetail, String ipAddress) {
        this.logId = null;
        this.adminId = adminId;
        this.operationType = operationType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.operationDetail = operationDetail;
        this.ipAddress = ipAddress;
        this.createdTime = Instant.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
