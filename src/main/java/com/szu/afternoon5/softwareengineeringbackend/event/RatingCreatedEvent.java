package com.szu.afternoon5.softwareengineeringbackend.event;

public record RatingCreatedEvent(Long ratingId, Long postId, Long userId) {}
