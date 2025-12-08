package com.szu.afternoon5.softwareengineeringbackend.dto;

import lombok.Data;

@Data
public class WechatSessionResponse {

    private String session_key;

    private String unionid;

    private String errmsg;

    private String openid;

    private Integer errcode;
}
