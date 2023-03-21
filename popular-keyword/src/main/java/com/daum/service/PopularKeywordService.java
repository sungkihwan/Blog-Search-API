package com.daum.service;

import com.daum.entity.PopularKeyword;
import com.daum.event.BlogSearchKeywordUpdateEvent;
import com.daum.repository.PopularKeywordCustomRepository;
import com.daum.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {

//    private final int cacheRate;

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
    @Scheduled(fixedRate = 10000)
    @Caching(evict = {
            @CacheEvict(value = "top10Keywords", allEntries = true)
    })
    public void cacheTop10Keywords() {
        getTop10Keywords();
    }

    @EventListener
    @Transactional
    public void handleKeywordUpdateEvent(BlogSearchKeywordUpdateEvent event) {
        updateKeyword(event.getKeyword());
    }

//    @Transactional
    public void updateKeyword(String keyword) {
        int updated = popularKeywordRepository.updateKeywordCount(keyword);
        if (updated == 0) {
            popularKeywordCustomRepository.upsertKeyword(keyword, 1L);
        }
    }

    // Test 목적
    public PopularKeyword findByKeyword(String keyword) {
        return popularKeywordRepository.findByKeyword(keyword)
                .orElseThrow();
    }
}
