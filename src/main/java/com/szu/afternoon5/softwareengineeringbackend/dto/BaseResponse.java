package com.szu.afternoon5.softwareengineeringbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.Data;

/**
 * 通用响应体模型，封装错误码与错误消息。
 * <p>
 * 若后续需要返回数据载荷或追踪请求 ID，可在此扩展泛型或新增字段。
 */
@Data
public class BaseResponse {

    /**
     * 业务错误码，20000 约定为成功。
     */
    @JsonProperty("err_code")
    private Integer errCode;

    /**
     * 错误提示信息，成功时为空字符串。
     */
    @JsonProperty("err_msg")
    private String errMsg;

    public BaseResponse() {
        this.errCode = 20000;
        this.errMsg = "成功";
    }

    public BaseResponse(ErrorCode errorCode) {
        this.errCode = errorCode.code();
        this.errMsg = errorCode.defaultMessage();
    }

    public BaseResponse(ErrorCode errorCode, String errMsg) {
        this.errCode = errorCode.code();
        this.errMsg = errMsg;
    }
}
