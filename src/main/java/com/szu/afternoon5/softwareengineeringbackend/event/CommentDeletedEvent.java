package com.szu.afternoon5.softwareengineeringbackend.event;

public record CommentDeletedEvent(Long commentId, Long postId, Long userId) {}
