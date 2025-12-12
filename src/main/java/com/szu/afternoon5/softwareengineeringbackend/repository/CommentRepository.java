package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据帖子ID查询未删除的评论列表，支持分页。
     */
    Page<Comment> findByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    /**
     * 根据用户ID查询未删除的评论列表，支持分页。
     */
    Page<Comment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
