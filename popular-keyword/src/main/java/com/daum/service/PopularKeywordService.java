package com.daum.service;

import com.daum.common.exception.KeywordNotFoundException;
import com.daum.common.exception.KeywordUpdateException;
import com.daum.entity.PopularKeyword;
import com.daum.repository.PopularKeywordCustomRepository;
import com.daum.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularKeywordService {

    private final PopularKeywordRepository popularKeywordRepository;

    private final PopularKeywordCustomRepository popularKeywordCustomRepository;

    @Cacheable(value = "top10Keywords")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordRepository.findTop10ByOrderByCountDesc();
    }

    // 10초 주기로 캐시를 새로 넣음 -> 기준에 따라 변경
    // 데이터가 많아질 경우
    // Redis SortedSet
    // Redis Cache & Batch
    // Window 필요한 경우 or 실시간성 -> Batch & Stream
    @Async
    @Scheduled(fixedRate = 5000)
    @Caching(evict = {
            @CacheEvict(value = "top10Keywords", allEntries = true)
    })
    public void cacheTop10Keywords() {
        getTop10Keywords();
    }

    @Transactional
    public void updateKeyword(String keyword) {
        try {
            int updated = popularKeywordRepository.updateKeywordCount(keyword);
            if (updated == 0) {
                popularKeywordCustomRepository.upsertKeyword(keyword, 1L);
            }
        } catch (Exception e) {
            log.error("키워드 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new KeywordUpdateException("키워드 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    // Test 목적
    public PopularKeyword findByKeyword(String keyword) {
        return popularKeywordRepository.findByKeyword(keyword)
                .orElseThrow(()-> new KeywordNotFoundException("키워드를 찾을 수 없습니다."));

    }
}
