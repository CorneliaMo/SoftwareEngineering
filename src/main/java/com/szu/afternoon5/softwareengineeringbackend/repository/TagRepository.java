package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
}
