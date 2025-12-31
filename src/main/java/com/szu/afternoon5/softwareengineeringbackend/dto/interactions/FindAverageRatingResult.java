package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 平均评分聚合结果
 */
@Data
@AllArgsConstructor
public class FindAverageRatingResult {

    private Double averageRating;

    private Long ratingCount;

}
