package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 标签信息对象，后期可以加入引用数等字段
 */
@Data
@AllArgsConstructor
public class TagInfo {

    private String name;

}
