package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 管理员列表响应体
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminListResponse extends PageMeta {

    @JsonProperty("admins")
    private List<AdminDetail> admins;

    public AdminListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize, List<AdminDetail> admins) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.admins = admins;
    }
}
