package com.daum.search.service;

import com.daum.search.entity.PopularKeyword;
import com.daum.search.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
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
public class PopularKeywordService {

    private final PopularKeywordRepository popularKeywordRepository;

    @Cacheable(value = "top10Keywords")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordRepository.findTop10ByOrderByCountDesc();
    }

    @Async
    @Scheduled(fixedRate = 20000) // 20초 주기로 캐시를 새로 넣음
    @Caching(evict = {
            @CacheEvict(value = "top10Keywords", allEntries = true)
    })
    public void cacheTop10Keywords() {
        getTop10Keywords();
    }

//    @Caching(evict = {
//            @CacheEvict(value = "top10Keywords", allEntries = true)
//    })
    @Transactional
    public void updateKeywordCount(String keyword) {
        int updatedRowCount = popularKeywordRepository.updateKeywordCount(keyword);
        if (updatedRowCount == 0) {
            popularKeywordRepository.save(new PopularKeyword(keyword, 1L));
        }
    }
}
