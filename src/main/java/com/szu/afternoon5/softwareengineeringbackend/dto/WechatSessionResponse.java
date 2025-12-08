package com.szu.afternoon5.softwareengineeringbackend.dto;

import lombok.Data;

/**
 * 微信登录 code2Session 响应模型，对应微信开放平台返回字段。
 * <p>
 * 如需兼容更多返回参数或错误场景，可在此补充字段并结合 WebClient 做统一反序列化。
 */
@Data
public class WechatSessionResponse {

    /**
     * 会话密钥，后续可用于数据解密。
     */
    private String session_key;

    /**
     * 微信 UnionId，便于跨应用识别用户。
     */
    private String unionid;

    /**
     * 错误消息，微信返回的原始描述。
     */
    private String errmsg;

    /**
     * 当前登录态对应的 OpenId。
     */
    private String openid;

    /**
     * 错误码，0 或缺省代表成功。
     */
    private Integer errcode;
}
