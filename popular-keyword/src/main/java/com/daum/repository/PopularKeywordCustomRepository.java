package com.daum.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PopularKeywordCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    public void insertKeyword(String keyword, Long count) {
        entityManager.createNativeQuery("INSERT INTO popular_keyword (keyword, count) VALUES (:keyword, :count)")
                .setParameter("keyword", keyword)
                .setParameter("count", count)
                .executeUpdate();
    }
}
