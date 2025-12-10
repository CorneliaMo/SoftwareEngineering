package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag,Long> {
    List<PostTag> findAllByPostId(Long postId);

    void deleteByPostId(Long postId);

    void deleteByPostIdAndTagIdIn(Long postId, Collection<Long> tagIds);
}
