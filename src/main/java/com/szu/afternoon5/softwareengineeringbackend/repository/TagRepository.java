package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    /**
     * 根据一组归一化名称批量查询标签实体。
     */
    List<Tag> findAllByNormalizedNameIn(Collection<String> normalizedNames);
}
