package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.entity.Tag;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostTag;
import com.szu.afternoon5.softwareengineeringbackend.repository.TagRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务：
 * - 负责标签的标准化、创建与复用
 * - 负责帖子与标签之间多对多关系的建立
 */
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    /**
     * 为指定帖子绑定一组标签：
     * 1. 对传入的标签字符串进行标准化（去空白、统一大小写等）
     * 2. 已存在的标签直接复用，不存在的自动创建 Tag 记录
     * 3. 将标准化后的标签映射为 TagId 列表
     * 4. 批量创建 PostTag 关联（跳过已存在的关联）
     *
     * @param postId 帖子主键
     * @param tags   原始标签列表（可包含重复、大小写混合等）
     * @return 绑定到该帖子的标签 ID 列表（去重后）
     */
    @Transactional
    public List<Long> bindTagsToPost(Long postId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 预处理 & 标准化：去空白、去掉空标签、统一大小写
        // 使用 LinkedHashMap 保证顺序 + 去重
        Map<String, String> normalizedToDisplayName = new LinkedHashMap<>();
        for (String raw : tags) {
            if (raw == null) {
                continue;
            }
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String normalized = normalizeTag(trimmed);
            if (!normalized.isEmpty()) {
                // 如果同一个 normalized 出现多次，只保留第一个的展示名
                normalizedToDisplayName.putIfAbsent(normalized, trimmed);
            }
        }

        if (normalizedToDisplayName.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 按原始顺序构造 tagId 列表
        List<Long> tagIds = insertMissedTags(normalizedToDisplayName);

        // 5. 查询该帖子已有的标签关联，避免重复插入 PostTag
        List<PostTag> existingRelations = postTagRepository.findAllByPostId(postId);
        Set<Long> existingTagIds = existingRelations.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toSet());

        List<PostTag> relationsToCreate = new ArrayList<>();
        for (Long tagId : tagIds) {
            if (!existingTagIds.contains(tagId)) {
                relationsToCreate.add(new PostTag(postId, tagId));
            }
        }

        if (!relationsToCreate.isEmpty()) {
            postTagRepository.saveAll(relationsToCreate);
        }

        return tagIds;
    }

    /**
     * 更新指定帖子的标签列表（全量替换语义，增量落库）。
     * <p>
     * 步骤：
     * 1. 对传入标签文本做标准化，得到去重后的 normalizedName 集合；
     * 2. 根据 normalizedName 查询/创建 Tag，并映射得到新的 tagId 集合；
     * 3. 查询该帖子当前已有的标签关系，得到 oldTagIds；
     * 4. 计算差集：
     *    - toAdd   = newTagIds - oldTagIds  → 需要新增的 PostTag；
     *    - toRemove = oldTagIds - newTagIds → 需要删除的 PostTag；
     * 5. 只对差异部分进行增删操作；
     * <p>
     * 注意：不会删除 Tag 实体本身，只会删除 PostTag 关系。
     *
     * @param postId  帖子主键
     * @param rawTags 前端传入的原始标签文本列表
     * @return 更新后该帖子绑定的标签 ID（去重后）
     */
    @Transactional
    public List<Long> updatePostTags(Long postId, List<String> rawTags) {
        // ------- 0. 处理空列表：视为清空该帖子的所有标签 -------
        if (rawTags == null || rawTags.isEmpty()) {
            // 只删关系，不删 Tag 本身
            postTagRepository.deleteByPostId(postId);
            return Collections.emptyList();
        }

        // ------- 1. 归一化标签文本，得到 normalizedName -> displayName 映射 -------
        // 用 LinkedHashMap 保证去重且保留出现顺序
        Map<String, String> normalizedToDisplayName = new LinkedHashMap<>();
        for (String raw : rawTags) {
            if (raw == null) {
                continue;
            }
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String normalized = normalizeTag(trimmed);
            if (normalized.isEmpty()) {
                continue;
            }
            // 同一个 normalized 多次出现时，只保留第一次的展示名
            normalizedToDisplayName.putIfAbsent(normalized, trimmed);
        }

        if (normalizedToDisplayName.isEmpty()) {
            postTagRepository.deleteByPostId(postId);
            return Collections.emptyList();
        }

        // ------- 3. 构造新的 tagId 集合（保持 normalizedNames 的顺序） -------

        List<Long> newTagIdList = insertMissedTags(normalizedToDisplayName);

        Set<Long> newTagIds = new HashSet<>(newTagIdList);

        // ------- 4. 查询帖子当前已有的标签关系，得到 oldTagIds -------

        List<PostTag> existingRelations = postTagRepository.findAllByPostId(postId);
        Set<Long> oldTagIds = existingRelations.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toSet());

        // ------- 5. 做集合运算，得到需要增删的关系 -------

        // 需要新增的：新集合有、旧集合没有
        Set<Long> toAdd = new HashSet<>(newTagIds);
        toAdd.removeAll(oldTagIds);

        // 需要删除的：旧集合有、新集合没有
        Set<Long> toRemove = new HashSet<>(oldTagIds);
        toRemove.removeAll(newTagIds);

        // ------- 6. 执行增量更新 PostTag 关系 -------

        if (!toRemove.isEmpty()) {
            postTagRepository.deleteByPostIdAndTagIdIn(postId, toRemove);
        }

        if (!toAdd.isEmpty()) {
            List<PostTag> relationsToCreate = toAdd.stream()
                    .map(tagId -> new PostTag(postId, tagId))
                    .collect(Collectors.toList());
            postTagRepository.saveAll(relationsToCreate);
        }

        // 返回这次更新后帖子应该绑定的标签 ID（去重后）
        return newTagIdList;
    }

    /**
     * 标签标准化逻辑：
     * - 去除前后空白
     * - 折叠中间的多个空白为单个空格
     * - 转为小写
     * - 进行 Unicode 归一化，避免全角/半角等差异
     * - 可以根据业务需要去掉前导 # 等字符
     *
     * @param raw 原始标签
     * @return 标准化后的标签字符串
     */
    private String normalizeTag(String raw) {
        String s = raw.trim();

        // 去掉前导 #（如 #tag → tag）
        if (s.startsWith("#")) {
            s = s.substring(1);
        }

        // Unicode 归一化（可选）
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);

        // 折叠中间多空白为单空格
        s = s.replaceAll("\\s+", " ");

        // 统一小写
        s = s.toLowerCase(Locale.ROOT);

        return s;
    }

    /**
     * 根据标准化名称批量补齐标签并返回对应ID列表。
     *
     * @param normalizedToDisplayName 标准化名称到展示名的映射
     * @return 对应的标签ID列表
     */
    private List<Long> insertMissedTags(Map<String, String> normalizedToDisplayName) {
        Set<String> normalizedNames = normalizedToDisplayName.keySet();

        // ------- 2. 根据 normalizedName 查询已有 Tag，并创建缺失的 Tag -------

        // 2.1 查询已存在的 Tag
        List<Tag> existingTags = tagRepository.findAllByNormalizedNameIn(normalizedNames);
        Map<String, Tag> normalizedToTag = existingTags.stream()
                .collect(Collectors.toMap(Tag::getNormalizedName, t -> t));

        // 2.2 为不存在的 normalizedName 创建新 Tag
        List<Tag> tagsToCreate = new ArrayList<>();
        for (String normalizedName : normalizedNames) {
            if (!normalizedToTag.containsKey(normalizedName)) {
                String displayName = normalizedToDisplayName.get(normalizedName);
                Tag tag = new Tag(displayName, normalizedName);
                // 如果完全依赖 @PrePersist，这行可以省略；否则这里兜底
                tag.setCreatedTime(Instant.now());
                tagsToCreate.add(tag);
            }
        }

        if (!tagsToCreate.isEmpty()) {
            List<Tag> created = tagRepository.saveAll(tagsToCreate);
            for (Tag tag : created) {
                normalizedToTag.put(tag.getNormalizedName(), tag);
            }
        }

        // ------- 3. 构造新的 tagId 集合（保持 normalizedNames 的顺序） -------

        return normalizedNames.stream()
                .map(n -> {
                    Tag tag = normalizedToTag.get(n);
                    if (tag == null) {
                        throw new IllegalStateException("Tag not found after creation: " + n);
                    }
                    return tag.getTagId();
                })
                .collect(Collectors.toList());
    }
}
