package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia,Long> {

    /**
     * 查询帖子绑定的所有媒体。
     */
    List<PostMedia> findAllByPostId(Long postId);
}
