package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * 管理员操作日志实体，记录后台动作及目标对象。
 * <p>
 * 未来可拓展地理位置、设备信息、差异化内容快照等字段，便于审计追踪。
 */
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

    /**
     * 记录一次后台操作的构造器，保存行为类型、目标及来源 IP。
     */
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

    /**
     * 落库前补全创建时间，确保日志时间线准确。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
