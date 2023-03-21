package com.daum.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Slf4j
public class PopularKeywordCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    public void upsertKeyword(String keyword, Long count) {
        try {
            entityManager
                    .createNativeQuery("INSERT INTO popular_keyword (keyword, count) VALUES (:keyword, :count) ON DUPLICATE KEY UPDATE count = count + :count")
                    .setParameter("keyword", keyword)
                    .setParameter("count", count)
                    .executeUpdate();
        } catch (Exception e) {
            log.error(
                    "popular_keyword 테이블에 데이터를 삽입하거나 업데이트하는 도중 오류가 발생하였습니다. 에러 내용: {}",
                    e.getMessage());
        }
    }
}
