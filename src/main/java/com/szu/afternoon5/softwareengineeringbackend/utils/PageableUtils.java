package com.szu.afternoon5.softwareengineeringbackend.utils;

import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PageableUtils {

    public boolean validateSortBy(String sortBy, List<String> availableColumns) {
        return availableColumns.contains(sortBy);
    }

    public boolean validateOrder(String order) {
        return Arrays.asList("ASC", "DESC").contains(order);
    }

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

    public Pageable buildPageable(List<String> sortColumns, int currentPage, int pageSize, String sortBy, String order) throws BusinessException {
        Sort sort = buildSort(sortColumns, sortBy, order);
        return PageRequest.of(currentPage, pageSize, sort);
    }
}
