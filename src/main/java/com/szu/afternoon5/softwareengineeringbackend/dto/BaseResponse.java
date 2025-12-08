package com.szu.afternoon5.softwareengineeringbackend.dto;

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
    private Integer errCode;

    /**
     * 错误提示信息，成功时为空字符串。
     */
    private String errMsg;

    public BaseResponse() {
        this.errCode = 20000;
        this.errMsg = "";
    }

    public BaseResponse(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
