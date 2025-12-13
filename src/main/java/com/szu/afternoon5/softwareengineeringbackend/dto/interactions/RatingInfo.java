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

    /**
     * 平均评分，所有用户评分的平均值。
     */
    @JsonProperty("average_rating")
    private Double averageRating;

    /**
     * 评分总人数，参与评分的用户数量。
     */
    @JsonProperty("rating_count")
    private Integer ratingCount;

    /**
     * 当前用户评分，当前登录用户对该帖子的评分值。
     */
    @JsonProperty("my_rating")
    private Integer myRating;

    public RatingInfo(Double averageRating, Integer ratingCount, Integer myRating) {
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.myRating = myRating;
    }
}