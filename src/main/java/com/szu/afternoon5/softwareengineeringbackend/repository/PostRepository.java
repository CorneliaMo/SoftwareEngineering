package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 根据用户ID和删除状态查询帖子列表，支持分页。
     */
    Page<Post> findByUserIdAndIsDeleted(Long userId, Boolean isDeleted, Pageable pageable);
}
