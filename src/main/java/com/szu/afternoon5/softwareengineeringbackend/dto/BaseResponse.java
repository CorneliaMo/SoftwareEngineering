package com.szu.afternoon5.softwareengineeringbackend.dto;

import lombok.Data;

@Data
public class BaseResponse {

    private Integer errCode;

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
