package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * 根据帖子ID和用户ID查询评分记录。
     */
    Optional<Rating> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 查询帖子的平均评分和评分人数。
     *
     * @param postId 帖子ID
     * @return Object[] 数组，第一个元素为平均评分，第二个元素为评分人数
     */
    @Query("SELECT AVG(r.ratingValue), COUNT(r) FROM Rating r WHERE r.postId = :postId")
    Object[] findAverageRatingAndCountByPostId(@Param("postId") Long postId);
}