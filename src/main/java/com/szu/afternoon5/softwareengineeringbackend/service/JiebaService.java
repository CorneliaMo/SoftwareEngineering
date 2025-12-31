package com.szu.afternoon5.softwareengineeringbackend.service;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局单例的 jieba 分词服务。
 *
 * <p>使用 com.huaban:jieba-analysis 库，构造 JiebaSegmenter 时需要加载词典，
 * 代价较高，因此本服务通过 Spring 单例注入确保词典只加载一次。</p>
 *
 * <p>提供精确模式与搜索模式两种分词方法，可直接用于全文搜索索引构建。</p>
 */
@Service
public class JiebaService {

    /**
     * 线程安全、全局只加载一次的分词器。
     */
    @Getter
    private final JiebaSegmenter segmenter;

    /**
     * 构建分词服务并加载自定义词典。
     */
    public JiebaService() {
        // 构造函数中加载词典（只发生一次）
        this.segmenter = new JiebaSegmenter();
        // ★ 加载自定义词典（classPath 方案）
        loadUserDict("src/main/resources/dict/sougou.dict");
    }

    /**
     * 精确模式分词（JiebaSegmenter.SegMode.INDEX）。
     *
     * @param text 文本
     * @return 分词结果列表
     */
    public List<String> cutForIndex(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return segmenter.process(text, JiebaSegmenter.SegMode.INDEX)
                .stream()
                .map(x -> x.word)
                .collect(Collectors.toList());
    }

    /**
     * 搜索模式分词（更细粒度）。适用于搜索索引。
     */
    public List<String> cutForSearch(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return segmenter.process(text, JiebaSegmenter.SegMode.SEARCH)
                .stream()
                .map(x -> x.word)
                .collect(Collectors.toList());
    }

    /**
     * 从 classpath 加载用户词典，每行一个词。
     * 例如 src/main/resources/dict/user_dict.txt
     */
    private void loadUserDict(String dictPath) {
        Path path = Paths.get(dictPath);
        WordDictionary wordDict = WordDictionary.getInstance();
        wordDict.loadUserDict(path);
    }
}
