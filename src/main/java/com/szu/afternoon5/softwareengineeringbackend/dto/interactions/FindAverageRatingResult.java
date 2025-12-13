package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindAverageRatingResult {

    private Double averageRating;

    private Long ratingCount;

}
