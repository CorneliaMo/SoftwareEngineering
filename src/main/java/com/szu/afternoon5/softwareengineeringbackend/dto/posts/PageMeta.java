package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 分页元数据，提供分页查询的公共信息。
 */
@Data
public class PageMeta {

    @JsonProperty("total_page")
    private Integer totalPage;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("page_size")
    private Integer pageSize;
}
