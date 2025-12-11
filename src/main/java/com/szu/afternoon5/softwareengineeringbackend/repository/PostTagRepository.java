package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag,Long> {
    /**
     * 查询指定帖子关联的标签记录。
     */
    List<PostTag> findAllByPostId(Long postId);

    /**
     * 根据帖子删除其所有标签绑定关系。
     */
    void deleteByPostId(Long postId);

    /**
     * 删除帖子与部分标签的绑定关系。
     */
    void deleteByPostIdAndTagIdIn(Long postId, Collection<Long> tagIds);
}
