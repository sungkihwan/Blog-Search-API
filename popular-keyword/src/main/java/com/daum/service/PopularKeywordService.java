package com.daum.service;

import com.daum.entity.PopularKeyword;
import com.daum.repository.PopularKeywordCustomRepository;
import com.daum.repository.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // 10초 주기로 캐시를 새로 넣음
    // 데이터가 많아질 경우 -> Redis SortedSet
    // 실시간 Window 필요한 경우 -> Batch & Stream
    @Async
    @Scheduled(fixedRate = 10000)
    @Caching(evict = {
            @CacheEvict(value = "top10Keywords", allEntries = true)
    })
    public void cacheTop10Keywords() {
        getTop10Keywords();
    }

//    @Caching(evict = {
//            @CacheEvict(value = "top10Keywords", allEntries = true)
//    })
//    @Transactional
//    public void updateKeywordCount(String keyword) {
//        popularKeywordRepository.updateKeywordCount(keyword);
//    }

    @Transactional
    public void insertKeyword(String keyword) {
        popularKeywordRepository.save(new PopularKeyword(keyword, 1L));
    }

//    @Transactional
//    public void updateKeywordCount(String keyword) {
//        Optional<PopularKeyword> lockedPopularKeywordOpt = popularKeywordRepository.findByIdWithLock(keyword);
//
//        if (lockedPopularKeywordOpt.isPresent()) {
//            PopularKeyword popularKeyword = lockedPopularKeywordOpt.get();
//            popularKeyword.setCount(popularKeyword.getCount() + 1);
//            popularKeywordRepository.save(popularKeyword);
//        } else {
//            try {
//                popularKeywordCustomRepository.insertKeyword(keyword, 1L);
//            } catch (Exception e) {
//                // 이미 존재하는 경우, 재귀적으로 메소드를 호출하여 업데이트를 시도합니다.
//                updateKeywordCount(keyword);
//            }
//        }
//    }

    public PopularKeyword findByKeyword(String keyword) {
        return popularKeywordRepository.findByKeyword(keyword)
                .orElseThrow();
    }
}
