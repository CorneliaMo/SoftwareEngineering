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

    /**
     * 平均评分，提交评分后重新计算的平均值。
     */
    @JsonProperty("average_rating")
    private Double averageRating;

    /**
     * 评分总人数，更新后的参与评分用户数量。
     */
    @JsonProperty("rating_count")
    private Integer ratingCount;

    public SubmitRatingResponse(Double averageRating, Integer ratingCount) {
        super();
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }
}
