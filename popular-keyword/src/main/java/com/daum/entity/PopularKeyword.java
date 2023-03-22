package com.daum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "popular_keyword",
        indexes = @Index(name = "idx_popular_keyword_count", columnList = "count")
)
public class PopularKeyword {

    @Id
    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "count", nullable = false)
    private Long count;
}
