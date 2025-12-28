package com.szu.afternoon5.softwareengineeringbackend.event;

public record RatingDeletedEvent(Long ratingId, Long postId, Long userId) {}
