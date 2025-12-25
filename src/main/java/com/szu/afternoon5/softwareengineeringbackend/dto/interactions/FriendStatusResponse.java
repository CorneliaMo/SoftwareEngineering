package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 好友关系响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FriendStatusResponse extends BaseResponse {

    @JsonProperty("friend")
    private Boolean friend;

    public FriendStatusResponse(Boolean friend) {
        super();
        this.friend = friend;
    }
}
