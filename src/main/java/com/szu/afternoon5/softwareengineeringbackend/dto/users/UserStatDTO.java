package com.szu.afternoon5.softwareengineeringbackend.dto.users;

import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Data
public class UserStatDTO {

    private OffsetDateTime createdTime;

    private Integer postCount;

    private Integer ratingCount;

    private Integer commentCount;

    public UserStatDTO(User user) {
        this.createdTime = user.getCreatedTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        this.postCount = user.getPostCount() != null ? user.getPostCount() : 0;
        this.ratingCount = user.getRatingCount() != null ? user.getRatingCount() : 0;
        this.commentCount = user.getCommentCount() != null ? user.getCommentCount() : 0;
    }
}
