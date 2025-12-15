package com.szu.afternoon5.softwareengineeringbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 用户统计信息响应体。
 * 对应接口文档中 GET /users/{user_id}/stat 接口的完整响应结构。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserStatResponse extends BaseResponse {

    @JsonProperty("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant createdTime;

    @JsonProperty("post_count")
    private Integer postCount;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public UserStatResponse(Instant createdTime, Integer postCount,
                            Integer ratingCount, Integer commentCount) {
        super();
        this.createdTime = createdTime;
        this.postCount = postCount;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }

    public UserStatResponse() {
        super();
    }
}