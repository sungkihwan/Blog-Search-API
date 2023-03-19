package com.daum.search.repository;

import com.daum.search.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, String> {

    List<PopularKeyword> findTop10ByOrderByCountDesc();

    @Modifying
    @Query("UPDATE PopularKeyword p SET p.count = p.count +1 WHERE p.keyword = :keyword")
    int updateKeywordCount(@Param("keyword") String keyword);
}
