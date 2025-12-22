package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.TagInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    /**
     * 根据一组归一化名称批量查询标签实体。
     */
    List<Tag> findAllByNormalizedNameIn(Collection<String> normalizedNames);

    /**
     * 通过帖子id查询对应的标签列表
     *
     * @param postId 帖子id
     * @return 标签显示名称列表
     */
    @Query("""
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.posts.TagInfo(t.name) FROM Tag t
    WHERE t.tagId IN (
        SELECT pt.tagId FROM PostTag pt
        WHERE (:postId IS NOT NULL AND pt.postId = :postId)
    )
""")
    List<TagInfo> findTagsNameByPostId(Long postId);
}
