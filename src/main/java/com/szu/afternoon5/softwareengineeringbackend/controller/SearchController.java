package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索接口控制器，负责统一的检索入口。
 * <p>
 * 后续可对接全文搜索或标签过滤能力，并支持排序、分页等查询参数的扩展。
 */
@RestController
@RequestMapping("/search")
public class SearchController {

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
    public PostListResponse searchByDate(@RequestParam(value = "start_date", required = false) String startDate,
                                         @RequestParam(value = "end_date", required = false) String endDate,
                                         @RequestParam(value = "user_id", required = false) Integer userId,
                                         @RequestParam(value = "current_page", required = false) Integer currentPage,
                                         @RequestParam(value = "page_size", required = false) Integer pageSize) {
        return null;
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
    public PostListResponse searchByTag(@RequestParam(value = "tag", required = false) String tag,
                                        @RequestParam(value = "user_id", required = false) Integer userId,
                                        @RequestParam(value = "current_page", required = false) Integer currentPage,
                                        @RequestParam(value = "page_size", required = false) Integer pageSize) {
        return null;
    }

    /**
     * 综合搜索帖子列表。
     *
     * @param keyword     关键词
     * @param type        内容类型过滤
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 分页帖子摘要列表
     */
    @GetMapping
    public PostListResponse search(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "current_page", required = false) Integer currentPage,
                                   @RequestParam(value = "page_size", required = false) Integer pageSize) {
        return null;
    }
}
