package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.ConversationOneToOneMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationOneToOneMapRepository extends JpaRepository<ConversationOneToOneMap, Long> {
    /**
     * 判断指定用户对之间的一对一会话映射是否存在。
     */
    boolean existsByUserLowIdAndUserHighId(Long userLowId, Long userHighId);
}
