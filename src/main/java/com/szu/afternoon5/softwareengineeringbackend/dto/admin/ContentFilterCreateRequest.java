package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.ContentFilter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 新增内容过滤器请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ContentFilterCreateRequest {

    /**
     * 过滤内容
     */
    @NotBlank
    @JsonProperty("filter_content")
    private String filterContent;

    /**
     * 过滤类型
     */
    @NotNull
    @JsonProperty("filter_type")
    private ContentFilter.FilterType filterType;

    /**
     * 过滤级别
     */
    @NotNull
    @JsonProperty("level")
    private ContentFilter.FilterLevel level;

    /**
     * 分类
     */
    @NotBlank
    @JsonProperty("category")
    private String category;
}
