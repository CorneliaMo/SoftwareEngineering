package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.ConversationOneToOneMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationOneToOneMapRepository extends JpaRepository<ConversationOneToOneMap, Long> {
    boolean existsByUserLowIdAndUserHighId(Long userLowId, Long userHighId);
}
