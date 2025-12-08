package com.szu.afternoon5.softwareengineeringbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通用操作响应，使用蛇形字段名以匹配 OpenAPI 描述。
 */
@Data
public class SimpleResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;
}
