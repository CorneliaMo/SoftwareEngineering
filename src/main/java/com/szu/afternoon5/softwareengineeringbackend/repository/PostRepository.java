package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query(value = """
    SELECT p, pm FROM Post p
    JOIN PostMedia pm ON p.coverMediaId = pm.mediaId
    WHERE p.userId = :userId AND p.isDeleted = :isDeleted
""")
    Page<PostWithCover> findByUserIdAndIsDeletedWithCover(Long userId, boolean isDeleted, Pageable pageable);
}
