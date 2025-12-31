package com.szu.afternoon5.softwareengineeringbackend.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户统计信息响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserStatResponse extends BaseResponse {

    @JsonProperty("user_stat")  // 包装成 user_stat 对象
    private UserStatDTO userStat;

    // 构造函数1
    public UserStatResponse(UserStatDTO userStat) {
        super();
        this.userStat = userStat;
    }
}
