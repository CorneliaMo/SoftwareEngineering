package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Table(schema = "tags")
public class Tag {

    @Id
    private Long tagId;

    private String name;

    private String normalizedName;

    private Instant createdTime;

    public Tag(String name, String normalizedName) {
        this.tagId = null;
        this.name = name;
        this.normalizedName = normalizedName;
        this.createdTime = Instant.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
