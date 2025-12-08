package com.szu.afternoon5.softwareengineeringbackend.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常封装，携带统一的错误码与错误信息。
 * <p>
 * 推荐在领域服务中抛出此异常，以便全局异常处理器转换为规范的响应体；后续可增加上下文数据便于排查。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * 使用默认错误描述构造业务异常。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * 携带自定义描述的业务异常，适用于需要附加字段信息的场景。
     */
    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}

