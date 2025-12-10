package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 标签实体，用于给帖子打标签并支持搜索过滤。
 * <p>
 * 可在此扩展描述、使用次数、颜色样式等字段，并为 normalizedName 添加唯一索引。
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String name;

    private String normalizedName;

    private Instant createdTime;

    /**
     * 创建标签的构造器，同时填充标准化名称以便去重。
     */
    public Tag(String name, String normalizedName) {
        this.tagId = null;
        this.name = name;
        this.normalizedName = normalizedName;
        this.createdTime = Instant.now();
    }

    /**
     * 持久化前补全创建时间。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
