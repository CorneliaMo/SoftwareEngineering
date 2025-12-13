package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.CommentInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据帖子ID查询未删除的评论列表，支持分页。
     */
    @Query(value = """
    SELECT c.userId, u.nickname, u.avatarUrl, c.commentId, c.postId, c.parentId, c.commentText, c.createdTime, c.updatedTime FROM Comment c
    JOIN User u ON c.userId = u.userId
    WHERE c.postId = :postId AND c.isDeleted = FALSE
""")
    Page<CommentInfo> findCommentInfoByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    /**
     * 根据用户ID查询未删除的评论列表，支持分页。
     */
    Page<Comment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
