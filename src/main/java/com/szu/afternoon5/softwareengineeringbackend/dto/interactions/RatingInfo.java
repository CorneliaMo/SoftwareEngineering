package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 评分信息视图，用于展示帖子评分统计。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RatingInfo {

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("my_rating")
    private Integer myRating;

    public RatingInfo(Double averageRating, Integer ratingCount, Integer myRating) {
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.myRating = myRating;
    }
}