package com.szu.afternoon5.softwareengineeringbackend.event;

public record CommentCreatedEvent(Long commentId, Long postId, Long userId) {}

