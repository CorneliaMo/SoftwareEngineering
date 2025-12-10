package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 提交评分响应体，返回更新后的评分统计。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SubmitRatingResponse extends BaseResponse {

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    public SubmitRatingResponse(Double averageRating, Integer ratingCount) {
        super();
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }
}