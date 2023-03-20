package com.daum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLInsert;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "popular_keyword")
@SQLInsert(sql="INSERT INTO popular_keyword(keyword, count) VALUES (?, ?) ON DUPLICATE KEY UPDATE count = count + 1" )
public class PopularKeyword {

    @Id
    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "count", nullable = false)
    private Long count;
}
