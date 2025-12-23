package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.ContentFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 更新内容过滤器请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ContentFilterUpdateRequest {

    /**
     * 过滤内容
     */
    @JsonProperty("filter_content")
    private String filterContent;

    /**
     * 过滤类型
     */
    @JsonProperty("filter_type")
    private ContentFilter.FilterType filterType;

    /**
     * 过滤级别
     */
    @JsonProperty("level")
    private ContentFilter.FilterLevel level;

    /**
     * 分类
     */
    @JsonProperty("category")
    private String category;
}
