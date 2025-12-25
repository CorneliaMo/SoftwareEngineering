package com.szu.afternoon5.softwareengineeringbackend.event;

public record FollowDeletedEvent(Long followerId, Long followeeId) {}
