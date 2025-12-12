package com.szu.afternoon5.softwareengineeringbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页响应组件，包含分页相关的通用字段。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PageMeta extends BaseResponse {

    /**
     * 总页数。
     */
    @JsonProperty("total_page")
    private Integer totalPage;

    /**
     * 所有元素总数。
     */
    @JsonProperty("total_count")
    private Integer totalCount;

    /**
     * 当前页码，索引从1开始。
     */
    @JsonProperty("current_page")
    private Integer currentPage;

    /**
     * 页面容量。
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    public PageMeta(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize) {
        super();
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
