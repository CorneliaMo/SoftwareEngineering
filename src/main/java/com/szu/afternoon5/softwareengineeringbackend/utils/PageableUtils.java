package com.szu.afternoon5.softwareengineeringbackend.utils;

import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 分页与排序辅助工具，统一构建 Spring Data 的 Pageable 与 Sort。
 * <p>
 * 若后续引入多字段排序或默认排序规则，可在此扩展校验与构造逻辑，避免控制层重复代码。
 */
@Component
public class PageableUtils {

    /**
     * 校验排序字段是否在允许列表中。
     */
    public boolean validateSortBy(String sortBy, List<String> availableColumns) {
        return availableColumns.contains(sortBy);
    }

    /**
     * 校验排序方向是否符合预期。
     */
    public boolean validateOrder(String order) {
        return Arrays.asList("ASC", "DESC").contains(order);
    }

    /**
     * 根据请求参数构建排序器，支持驼峰转换并抛出业务异常提示非法入参。
     */
    public Sort buildSort(List<String> sortColumns, String sortBy, String order) throws BusinessException {
        if (!validateSortBy(sortBy, sortColumns))
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "sort_by的列不存在");
        if (!validateOrder(order))
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "order只能是ASC或DESC");
        if (order.equalsIgnoreCase("ASC")) {
            return Sort.by(Sort.Direction.ASC, snakeToCamel(sortBy));
        } else {
            return Sort.by(Sort.Direction.DESC, snakeToCamel(sortBy));
        }
    }

    /**
     * 将下划线命名转为驼峰命名
     */
    public static String snakeToCamel(String snake) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                result.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return result.toString();
    }

    /**
     * 组装分页与排序参数，便于仓储层直接消费。
     */
    public Pageable buildPageable(List<String> sortColumns, int currentPage, int pageSize, String sortBy, String order) throws BusinessException {
        Sort sort = buildSort(sortColumns, sortBy, order);
        return PageRequest.of(currentPage, pageSize, sort);
    }
}
