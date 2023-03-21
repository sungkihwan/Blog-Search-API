package com.daum.repository;

import com.daum.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, String> {

    List<PopularKeyword> findTop10ByOrderByCountDesc();

    Optional<PopularKeyword> findByKeyword(String keyword);

    @Modifying
    @Query("UPDATE PopularKeyword p SET p.count = p.count + 1 WHERE p.keyword = :keyword")
    int updateKeywordCount(@Param("keyword") String keyword);

}
