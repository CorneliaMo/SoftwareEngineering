package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 关注/粉丝/好友列表响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InteractionUserListResponse extends PageMeta {

    @JsonProperty("users")
    private List<UserInfo> users;

    public InteractionUserListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize,
                                       List<UserInfo> users) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.users = users;
    }
}
