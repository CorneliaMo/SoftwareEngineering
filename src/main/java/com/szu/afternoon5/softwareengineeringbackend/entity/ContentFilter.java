package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "content_filters")
public class ContentFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer filterId;

    private String filterContent;

    @Enumerated(EnumType.STRING)
    private FilterType filterType;

    @Enumerated(EnumType.STRING)
    private FilterLevel level;

    private String category;

    public enum FilterType {
        word, regex
    }

    public enum FilterLevel {
        block, warn, log
    }

    public ContentFilter(String filterContent, FilterType filterType, FilterLevel filterLevel, String category) {
        this.filterId = null;
        this.filterContent = filterContent;
        this.filterType = filterType;
        this.level = filterLevel;
        this.category = category;
    }
}
