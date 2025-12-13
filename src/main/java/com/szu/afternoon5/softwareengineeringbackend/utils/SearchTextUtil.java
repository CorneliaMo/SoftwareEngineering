package com.szu.afternoon5.softwareengineeringbackend.utils;

import java.util.List;
import java.util.stream.Collectors;

public final class SearchTextUtil {

    private SearchTextUtil() {}

    /**
     * 将分词结果拼接为适合 to_tsvector 使用的字符串：
     * 例如 ["上海", "咖啡", "文化"] -> "上海 咖啡 文化"
     */
    public static String joinTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        return tokens.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
