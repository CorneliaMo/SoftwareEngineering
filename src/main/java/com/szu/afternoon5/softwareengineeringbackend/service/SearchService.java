package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.*;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostRepository;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索业务服务。
 *
 * <p>负责实现按日期、按标签以及全文检索三种搜索方式，
 * 其中全文搜索依赖 PostgreSQL tsvector + jieba 分词。</p>
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final JiebaService jiebaService;
    private final PageableUtils pageableUtils;

    /**
     * 按日期范围搜索帖子列表，可选按 userId 过滤。
     */
    public PostListResponse searchByDate(LocalDate startDate,
                                         LocalDate endDate,
                                         Long userId,
                                         Integer currentPage,
                                         Integer pageSize) {
        // TODO：LocalDate -> Instant（假设使用 UTC +0，按项目需要调整时区）
        var startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        var endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        // TODO：当前默认按帖子创建时间倒序排列，后续可以让前端自定义排序
        Pageable pageable = buildPostPageRequest(
                Math.max(0, currentPage - 1),
                Math.max(5, pageSize),
                "updated_time",
                "DESC");

        Page<PostWithCover> postWithCoverPage = postRepository.searchByDateRange(startInstant, endInstant, userId, pageable);

        return new PostListResponse(
                postWithCoverPage.getTotalPages(),
                (int) postWithCoverPage.getTotalElements(),
                postWithCoverPage.getNumber() + 1,
                postWithCoverPage.getSize(),
                postWithCoverPage.stream().map(postWithCover ->
                        new PostSummaryItem(new PostInfo(postWithCover.getPost()), new MediaInfo(postWithCover.getPostMedia()))
                ).toList()
        );
    }

    /**
     * 按标签搜索帖子列表，可选按 userId 过滤。
     */
    public PostListResponse searchByTag(String tag,
                                        Long userId,
                                        Integer currentPage,
                                        Integer pageSize) {

        // TODO：当前默认按帖子创建时间倒序排列，后续可以让前端自定义排序
        Pageable pageable = buildPostPageRequest(
                Math.max(0, currentPage - 1),
                Math.max(5, pageSize),
                "updated_time",
                "DESC");

        Page<PostWithCover> postWithCoverPage = postRepository.searchByTagName(tag, userId, pageable);

        return new PostListResponse(
                postWithCoverPage.getTotalPages(),
                (int) postWithCoverPage.getTotalElements(),
                postWithCoverPage.getNumber() + 1,
                postWithCoverPage.getSize(),
                postWithCoverPage.stream().map(postWithCover ->
                        new PostSummaryItem(new PostInfo(postWithCover.getPost()), new MediaInfo(postWithCover.getPostMedia()))
                ).toList()
        );
    }

    /**
     * 综合全文搜索。
     *
     * @param keyword  搜索关键字（原始字符串）
     */
    public PostListResponse search(String keyword,
                                   Integer currentPage,
                                   Integer pageSize) {

        Pageable pageable = PageRequest.of(
                Math.max(currentPage - 1, 0),
                Math.max(pageSize, 5)
        );

        // 1. 用 jieba 做搜索模式分词
        String queryText = buildFullTextQuery(keyword);

        // 2. 调用 Postgres 全文检索
        Page<PostWithCoverForNative> postWithCoverPage = postRepository.searchFullText(queryText, pageable);

        return new PostListResponse(
                postWithCoverPage.getTotalPages(),
                (int) postWithCoverPage.getTotalElements(),
                postWithCoverPage.getNumber() + 1,
                postWithCoverPage.getSize(),
                postWithCoverPage.stream().map(postWithCover ->
                    new PostSummaryItem(new PostInfo(postWithCover.getPost()), new MediaInfo(postWithCover.getPostMedia()))
                ).toList()
        );
    }

    /**
     * 构建全文检索用的 query 文本：使用 jieba 搜索模式分词，并用空格拼接。
     * 最终交给 plainto_tsquery('simple', :queryText) 处理。
     */
    private String buildFullTextQuery(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null; // 交给 SQL 里 (:queryText IS NULL) 分支处理
        }
        List<String> tokens = jiebaService.cutForSearch(keyword);
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private Pageable buildPostPageRequest(Integer currentPage, Integer pageSize, String sortBy, String order) {
        List<String> sortColumns = List.of("post_title", "created_time", "updated_time", "rating_count", "comment_count");
        return pageableUtils.buildPageable(sortColumns, currentPage, pageSize, sortBy, order);
    }
}
