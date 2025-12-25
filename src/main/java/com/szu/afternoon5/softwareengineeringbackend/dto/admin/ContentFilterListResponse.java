package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import com.szu.afternoon5.softwareengineeringbackend.entity.ContentFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 内容过滤器列表响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ContentFilterListResponse extends PageMeta {

    @JsonProperty("filters")
    private List<ContentFilter> filters;

    public ContentFilterListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize, List<ContentFilter> filters) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.filters = filters;
    }
}
