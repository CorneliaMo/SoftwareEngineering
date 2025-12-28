package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetFollowingTimelineResponse extends BaseResponse {

    private List<PostWithCover> posts;

    public GetFollowingTimelineResponse(List<PostWithCover> posts) {
        super();
        this.posts = posts;
    }

}
