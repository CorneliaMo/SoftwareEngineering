package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 提交评分请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SubmitRatingRequest {

    /**
     * 评分值，范围为 1-5。
     */
    @NotNull
    @Min(1)
    @Max(5)
    @JsonProperty("rating")
    private Integer rating;

    public SubmitRatingRequest(Integer rating) {
        this.rating = rating;
    }
}