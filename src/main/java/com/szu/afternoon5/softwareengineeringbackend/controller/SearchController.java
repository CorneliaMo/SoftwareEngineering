package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 搜索接口控制器，负责统一的检索入口。
 * <p>
 * 后续可对接全文搜索或标签过滤能力，并支持排序、分页等查询参数的扩展。
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    /**
     * 构建搜索控制器并注入依赖。
     */
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 按日期搜索帖子列表。
     *
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @param userId      用户过滤
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 分页帖子摘要列表
     */
    @GetMapping("/date")
    public PostListResponse searchByDate(@RequestParam(value = "start_date") LocalDate startDate,
                                         @RequestParam(value = "end_date") LocalDate endDate,
                                         @RequestParam(value = "user_id", required = false) Long userId,
                                         @RequestParam(value = "current_page") Integer currentPage,
                                         @RequestParam(value = "page_size") Integer pageSize) {
        return searchService.searchByDate(startDate, endDate, userId, currentPage, pageSize);
    }

    /**
     * 按标签搜索帖子列表。
     *
     * @param tag         标签名
     * @param userId      用户过滤
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 分页帖子摘要列表
     */
    @GetMapping("/tag")
    public PostListResponse searchByTag(@RequestParam(value = "tag") String tag,
                                        @RequestParam(value = "user_id", required = false) Long userId,
                                        @RequestParam(value = "current_page") Integer currentPage,
                                        @RequestParam(value = "page_size") Integer pageSize) {
        return searchService.searchByTag(tag, userId, currentPage, pageSize);
    }

    /**
     * 综合搜索帖子列表。
     *
     * @param keyword 可选，关键词
     * @param startDate 可选，最早发布日期（包含）
     * @param endDate 最晚发布日期（包含）
     * @param userId 可选，用户Id
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 分页帖子摘要列表，默认按发布时间倒序返回
     */
    @GetMapping
    public PostListResponse search(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "start_date", required = false) LocalDate startDate,
                                   @RequestParam(value = "end_date", required = false) LocalDate endDate,
                                   @RequestParam(value = "user_id", required = false) Long userId,
                                   @RequestParam(value = "current_page") Integer currentPage,
                                   @RequestParam(value = "page_size") Integer pageSize) {
        return searchService.search(keyword, startDate, endDate, userId, currentPage, pageSize);
    }
}
