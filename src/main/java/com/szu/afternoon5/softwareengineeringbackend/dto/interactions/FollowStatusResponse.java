package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 关注状态响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FollowStatusResponse extends BaseResponse {

    @JsonProperty("following")
    private Boolean following;

    @JsonProperty("mutual")
    private Boolean mutual;

    public FollowStatusResponse(Boolean following, Boolean mutual) {
        super();
        this.following = following;
        this.mutual = mutual;
    }
}
