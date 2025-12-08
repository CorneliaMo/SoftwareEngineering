package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
