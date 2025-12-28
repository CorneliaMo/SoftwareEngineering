package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FollowStatusResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FriendStatusResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.UserInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.FollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRecordRepository extends JpaRepository<FollowRecord, Long> {
    int deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FollowStatusResponse(
        ((
          SELECT COUNT(fr1)
          FROM FollowRecord fr1
          WHERE fr1.followerId = :me
            AND fr1.followeeId = :other
        ) > 0),
        (((
          SELECT COUNT(fr1)
          FROM FollowRecord fr1
          WHERE fr1.followerId = :me
            AND fr1.followeeId = :other
        ) > 0)
        AND
        ((
          SELECT COUNT(fr2)
          FROM FollowRecord fr2
          WHERE fr2.followerId = :other
            AND fr2.followeeId = :me
        ) > 0))
    )
    FROM User u
    WHERE u.userId = :me
""")
    FollowStatusResponse getFollowStatus(@Param("me") Long me, @Param("other") Long other);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.UserInfo(u.userId, u.nickname, u.avatarUrl, u.followingCount, u.followerCount) FROM FollowRecord fr
    JOIN User u ON fr.followeeId = u.userId
    WHERE fr.followerId = :userId
""")
    Page<UserInfo> getFollowingUserInfo(Long userId, Pageable pageable);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.UserInfo(u.userId, u.nickname, u.avatarUrl, u.followingCount, u.followerCount) FROM FollowRecord fr
    JOIN User u ON fr.followerId = u.userId
    WHERE fr.followeeId = :userId
""")
    Page<UserInfo> getFollowerUserInfo(Long userId, Pageable pageable);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FriendStatusResponse(
        (((
          SELECT COUNT(fr1)
          FROM FollowRecord fr1
          WHERE fr1.followerId = :me
            AND fr1.followeeId = :other
        ) > 0)
        AND
        ((
          SELECT COUNT(fr2)
          FROM FollowRecord fr2
          WHERE fr2.followerId = :other
            AND fr2.followeeId = :me
        ) > 0))
    )
    FROM User u
    WHERE u.userId = :me
""")
    FriendStatusResponse getFriendStatus(Long me, Long other);

    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.UserInfo(u.userId, u.nickname, u.avatarUrl, u.followingCount, u.followerCount)
    FROM User u
    WHERE EXISTS (
        SELECT 1 FROM FollowRecord f1
        WHERE f1.followerId = :me
          AND f1.followeeId = u.userId
    )
    AND EXISTS (
        SELECT 1 FROM FollowRecord f2
        WHERE f2.followerId = u.userId
          AND f2.followeeId = :me
    )
""")
    Page<UserInfo> getFriends(Long me, Pageable pageable);
}
