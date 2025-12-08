package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
