package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterCreateRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterUpdateRequest;
import com.szu.afternoon5.softwareengineeringbackend.entity.ContentFilter;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.ContentFilterRepository;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 内容过滤器服务，维护正则列表与AC自动机
 */
@Service
public class ContentFilterService {
    private static final Logger logger = LoggerFactory.getLogger(ContentFilterService.class);
    private final PageableUtils pageableUtils;
    private final ContentFilterRepository contentFilterRepository;
    private final Object filterLock = new Object();
    private final Map<Long, ContentFilter> filterCache = new ConcurrentHashMap<>();
    private final Map<Long, Pattern> regexPatternsById = new HashMap<>();
    private final Map<Long, String> wordPatternsById = new HashMap<>();
    private final Map<String, Set<Long>> wordIdsByPattern = new HashMap<>();
    private volatile List<RegexEntry> regexPatterns = List.of();
    private volatile Trie wordTrie = Trie.builder().build();

    /**
     * 构建内容过滤服务并注入依赖。
     */
    public ContentFilterService(PageableUtils pageableUtils, ContentFilterRepository contentFilterRepository) {
        this.pageableUtils = pageableUtils;
        this.contentFilterRepository = contentFilterRepository;
    }

    /**
     * 启动初始化过滤器缓存
     */
    @PostConstruct
    public void initFilters() {
        syncFromDatabase();
    }

    /**
     * 定时从数据库增量同步过滤器
     */
    @Scheduled(fixedDelayString = "${content_filter.sync_delay_ms:300000}")
    public void syncFromDatabase() {
        List<ContentFilter> latestFilters = contentFilterRepository.findAll();
        Map<Long, ContentFilter> latestMap = new HashMap<>();
        for (ContentFilter filter : latestFilters) {
            if (filter.getFilterId() != null) {
                latestMap.put(filter.getFilterId().longValue(), filter);
            }
        }

        synchronized (filterLock) {
            boolean regexChanged = false;
            boolean wordChanged = false;

            for (Map.Entry<Long, ContentFilter> entry : latestMap.entrySet()) {
                Long filterId = entry.getKey();
                ContentFilter latest = entry.getValue();
                ContentFilter existing = filterCache.get(filterId);
                if (existing == null || !isSameFilter(existing, latest)) {
                    if (existing != null) {
                        if (existing.getFilterType() == ContentFilter.FilterType.regex) {
                            regexPatternsById.remove(filterId);
                            regexChanged = true;
                        }
                        if (existing.getFilterType() == ContentFilter.FilterType.word) {
                            String removedWord = wordPatternsById.remove(filterId);
                            removeWordIndex(filterId, removedWord);
                            wordChanged = true;
                        }
                    }

                    if (latest.getFilterType() == ContentFilter.FilterType.regex) {
                        Pattern pattern = compileRegex(latest.getFilterContent(), false);
                        if (pattern != null) {
                            regexPatternsById.put(filterId, pattern);
                            regexChanged = true;
                        }
                    }
                    if (latest.getFilterType() == ContentFilter.FilterType.word) {
                        String word = normalizeWord(latest.getFilterContent(), false);
                        if (word != null) {
                            wordPatternsById.put(filterId, word);
                            wordIdsByPattern.computeIfAbsent(word, key -> new HashSet<>()).add(filterId);
                            wordChanged = true;
                        }
                    }
                    filterCache.put(filterId, latest);
                }
            }

            for (Long filterId : new ArrayList<>(filterCache.keySet())) {
                if (!latestMap.containsKey(filterId)) {
                    ContentFilter removed = filterCache.remove(filterId);
                    if (removed != null) {
                        if (removed.getFilterType() == ContentFilter.FilterType.regex) {
                            regexPatternsById.remove(filterId);
                            regexChanged = true;
                        }
                        if (removed.getFilterType() == ContentFilter.FilterType.word) {
                            String removedWord = wordPatternsById.remove(filterId);
                            removeWordIndex(filterId, removedWord);
                            wordChanged = true;
                        }
                    }
                }
            }

            if (regexChanged) {
                rebuildRegexPatterns();
            }
            if (wordChanged) {
                rebuildWordTrie();
            }
        }
    }

    /**
     * 获取过滤器列表（分页）
     */
    public ContentFilterListResponse getContentFilters(Integer currentPage, Integer pageSize) {
        List<String> sortColumns = List.of("filter_id", "filter_content", "filter_type", "level", "category");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "filter_id", "ASC");

        Page<ContentFilter> contentFilterPage = contentFilterRepository.findAll(pageable);
        return new ContentFilterListResponse(
                contentFilterPage.getTotalPages(),
                (int)contentFilterPage.getTotalElements(),
                contentFilterPage.getNumber() + 1,
                contentFilterPage.getSize(),
                contentFilterPage.getContent()
        );
    }

    /**
     * 新增过滤器并更新缓存
     */
    @Transactional
    public void createContentFilter(@Valid ContentFilterCreateRequest request) {
        validateRegexIfNeeded(request.getFilterType(), request.getFilterContent());
        ContentFilter contentFilter = new ContentFilter(
                request.getFilterContent(),
                request.getFilterType(),
                request.getLevel(),
                request.getCategory()
        );
        ContentFilter saved = contentFilterRepository.save(contentFilter);
        applyUpsert(saved, true);
    }

    /**
     * 更新过滤器并增量刷新缓存
     */
    @Transactional
    public void updateContentFilter(Long filterId, @Valid ContentFilterUpdateRequest request) {
        Optional<ContentFilter> contentFilterOptional = contentFilterRepository.findById(filterId);
        if (contentFilterOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "过滤器不存在");
        } else {
            ContentFilter contentFilter = contentFilterOptional.get();
            contentFilter.setFilterContent(Objects.requireNonNullElse(request.getFilterContent(), contentFilter.getFilterContent()));
            contentFilter.setFilterType(Objects.requireNonNullElse(request.getFilterType(), contentFilter.getFilterType()));
            contentFilter.setLevel(Objects.requireNonNullElse(request.getLevel(), contentFilter.getLevel()));
            contentFilter.setCategory(Objects.requireNonNullElse(request.getCategory(), contentFilter.getCategory()));
            validateRegexIfNeeded(contentFilter.getFilterType(), contentFilter.getFilterContent());
            ContentFilter saved = contentFilterRepository.save(contentFilter);
            applyUpsert(saved, true);
        }
    }

    /**
     * 删除过滤器并移除缓存
     */
    @Transactional
    public void deleteContentFilter(Long filterId) {
        Optional<ContentFilter> contentFilterOptional = contentFilterRepository.findById(filterId);
        if (contentFilterOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "过滤器不存在");
        } else {
            contentFilterRepository.delete(contentFilterOptional.get());
            removeFromCache(filterId);
        }
    }

    /**
     * 文本过滤检测
     * @param content 待检测文本
     * @return 命中详情
     */
    public FilterResult filter(String content) {
        if (content == null || content.isBlank()) {
            return new FilterResult(false, List.of());
        }

        List<FilterMatch> matches = new ArrayList<>();

        for (RegexEntry entry : regexPatterns) {
            if (entry == null || entry.pattern == null) {
                continue;
            }
            var matcher = entry.pattern.matcher(content);
            while (matcher.find()) {
                ContentFilter filter = filterCache.get(entry.filterId);
                matches.add(buildMatch(filter, entry.filterId, entry.ruleContent, matcher.start(), matcher.end(),
                        content.substring(matcher.start(), matcher.end())));
            }
        }

        List<Emit> emits = (List<Emit>) wordTrie.parseText(content);
        if (!emits.isEmpty()) {
            for (Emit emit : emits) {
                String keyword = emit.getKeyword();
                Set<Long> filterIds = wordIdsByPattern.getOrDefault(keyword, Set.of());
                if (filterIds.isEmpty()) {
                    continue;
                }
                for (Long filterId : filterIds) {
                    ContentFilter filter = filterCache.get(filterId);
                    matches.add(buildMatch(filter, filterId, keyword, emit.getStart(), emit.getEnd() + 1, keyword));
                }
            }
        }

        return new FilterResult(!matches.isEmpty(), matches);
    }

    /**
     * 更新过滤器缓存并按类型刷新索引
     */
    private void applyUpsert(ContentFilter filter, boolean strict) {
        if (filter == null || filter.getFilterId() == null) {
            return;
        }
        Long filterId = filter.getFilterId().longValue();
        synchronized (filterLock) {
            boolean regexChanged = false;
            boolean wordChanged = false;

            ContentFilter existing = filterCache.get(filterId);
            if (existing != null) {
                if (existing.getFilterType() == ContentFilter.FilterType.regex) {
                    regexPatternsById.remove(filterId);
                    regexChanged = true;
                }
                if (existing.getFilterType() == ContentFilter.FilterType.word) {
                    String removedWord = wordPatternsById.remove(filterId);
                    removeWordIndex(filterId, removedWord);
                    wordChanged = true;
                }
            }

            if (filter.getFilterType() == ContentFilter.FilterType.regex) {
                Pattern pattern = compileRegex(filter.getFilterContent(), strict);
                if (pattern != null) {
                    regexPatternsById.put(filterId, pattern);
                    regexChanged = true;
                }
            }
            if (filter.getFilterType() == ContentFilter.FilterType.word) {
                String word = normalizeWord(filter.getFilterContent(), strict);
                if (word != null) {
                    wordPatternsById.put(filterId, word);
                    wordIdsByPattern.computeIfAbsent(word, key -> new HashSet<>()).add(filterId);
                    wordChanged = true;
                }
            }

            filterCache.put(filterId, filter);
            if (regexChanged) {
                rebuildRegexPatterns();
            }
            if (wordChanged) {
                rebuildWordTrie();
            }
        }
    }

    /**
     * 从缓存中移除指定过滤器
     */
    private void removeFromCache(Long filterId) {
        if (filterId == null) {
            return;
        }
        synchronized (filterLock) {
            ContentFilter removed = filterCache.remove(filterId);
            if (removed == null) {
                return;
            }
            boolean regexChanged = false;
            boolean wordChanged = false;

            if (removed.getFilterType() == ContentFilter.FilterType.regex) {
                regexPatternsById.remove(filterId);
                regexChanged = true;
            }
            if (removed.getFilterType() == ContentFilter.FilterType.word) {
                String removedWord = wordPatternsById.remove(filterId);
                removeWordIndex(filterId, removedWord);
                wordChanged = true;
            }

            if (regexChanged) {
                rebuildRegexPatterns();
            }
            if (wordChanged) {
                rebuildWordTrie();
            }
        }
    }

    /**
     * 重建正则匹配列表
     */
    private void rebuildRegexPatterns() {
        List<RegexEntry> entries = new ArrayList<>();
        for (Map.Entry<Long, Pattern> entry : regexPatternsById.entrySet()) {
            Long filterId = entry.getKey();
            ContentFilter filter = filterCache.get(filterId);
            String ruleContent = filter == null ? null : filter.getFilterContent();
            entries.add(new RegexEntry(filterId, ruleContent, entry.getValue()));
        }
        regexPatterns = List.copyOf(entries);
    }

    /**
     * 重建关键词自动机
     */
    private void rebuildWordTrie() {
        Trie.TrieBuilder builder = Trie.builder();
        for (String word : wordIdsByPattern.keySet()) {
            builder.addKeyword(word);
        }
        wordTrie = builder.build();
    }

    /**
     * 判断过滤器内容是否一致
     */
    private boolean isSameFilter(ContentFilter left, ContentFilter right) {
        return Objects.equals(left.getFilterContent(), right.getFilterContent())
                && left.getFilterType() == right.getFilterType()
                && left.getLevel() == right.getLevel()
                && Objects.equals(left.getCategory(), right.getCategory());
    }

    /**
     * 校验过滤内容合法性
     */
    private void validateRegexIfNeeded(ContentFilter.FilterType filterType, String content) {
        if (filterType == ContentFilter.FilterType.regex) {
            compileRegex(content, true);
        } else if (filterType == ContentFilter.FilterType.word) {
            normalizeWord(content, true);
        }
    }

    /**
     * 编译正则表达式
     */
    private Pattern compileRegex(String content, boolean strict) {
        if (content == null) {
            if (strict) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, "过滤内容不能为空");
            }
            return null;
        }
        try {
            return Pattern.compile(content);
        } catch (PatternSyntaxException e) {
            if (strict) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, "正则表达式非法");
            }
            logger.warn("内容过滤器正则表达式非法: {}", content);
            return null;
        }
    }

    /**
     * 规范化关键词内容
     */
    private String normalizeWord(String content, boolean strict) {
        if (content == null || content.isBlank()) {
            if (strict) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, "过滤内容不能为空");
            }
            return null;
        }
        return content;
    }

    /**
     * 从关键词索引中移除指定过滤器
     */
    private void removeWordIndex(Long filterId, String word) {
        if (word == null) {
            return;
        }
        Set<Long> ids = wordIdsByPattern.get(word);
        if (ids == null) {
            return;
        }
        ids.remove(filterId);
        if (ids.isEmpty()) {
            wordIdsByPattern.remove(word);
        }
    }

    /**
     * 构建命中详情
     */
    private FilterMatch buildMatch(ContentFilter filter, Long filterId, String ruleContent,
                                   int start, int end, String matchText) {
        FilterMatch match = new FilterMatch();
        match.setFilterId(filterId);
        match.setFilterType(filter == null ? null : filter.getFilterType());
        match.setFilterContent(ruleContent);
        match.setMatchText(matchText);
        match.setStartIndex(start);
        match.setEndIndex(end);
        match.setLevel(filter == null ? null : filter.getLevel());
        match.setCategory(filter == null ? null : filter.getCategory());
        return match;
    }

    /**
     * 过滤命中结果
     */
    @Getter
    @ToString(callSuper = true)
    public static class FilterResult {
        private final boolean matched;
        private final List<FilterMatch> matches;

        /**
         * 构建过滤命中结果。
         */
        public FilterResult(boolean matched, List<FilterMatch> matches) {
            this.matched = matched;
            this.matches = matches;
        }
    }

    /**
     * 命中详情
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FilterMatch {
        private Long filterId;
        private ContentFilter.FilterType filterType;
        private String filterContent;
        private String matchText;
        private Integer startIndex;
        private Integer endIndex;
        private ContentFilter.FilterLevel level;
        private String category;
    }

    private static class RegexEntry {
        private final Long filterId;
        private final String ruleContent;
        private final Pattern pattern;

        /**
         * 构建正则缓存条目。
         */
        private RegexEntry(Long filterId, String ruleContent, Pattern pattern) {
            this.filterId = filterId;
            this.ruleContent = ruleContent;
            this.pattern = pattern;
        }
    }
}
