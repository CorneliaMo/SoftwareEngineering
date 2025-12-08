package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页元数据，提供分页查询的公共信息。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PageMeta extends BaseResponse {

    @JsonProperty("total_page")
    private Integer totalPage;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("page_size")
    private Integer pageSize;

    public PageMeta(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize) {
        super();
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageMeta(ErrorCode errorCode, String errMsg, Integer totalPage, Integer totalCount, Integer currentPage,
                    Integer pageSize) {
        super(errorCode, errMsg);
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
