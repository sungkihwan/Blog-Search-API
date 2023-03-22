package com.daum.controller;

import com.daum.entity.PopularKeyword;
import com.daum.event.BlogSearchKeywordUpdateEvent;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BlogSearchController {

    private final KakaoBlogSearchService kakaoBlogSearchService;

    private final PopularKeywordService popularKeywordService;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 블로그 검색 API
     * Kakao Open API 실패 시 Naver Open API 호출
     *
     * @param request 블로그 검색 요청 객체 : KakaoBlogSearchRequest
     * @return 블로그 검색 결과 : KakaoBlogSearchResponse
     */
    @GetMapping("/search/blog")
    public KakaoBlogSearchResponse searchBlog(
            @Valid KakaoBlogSearchRequest request
    ) {
        // popularKeywordService.updateKeyword(request.getQuery()); 동기식
        // 비동기식 pub - sub -> Message Broker (Kafka) 전환
        eventPublisher.publishEvent(new BlogSearchKeywordUpdateEvent(this, request.getQuery()));
        return kakaoBlogSearchService.search(request)
                .block(); // block 제거 -> Mono
    }

    /**
     * 인기 검색어 탑 10 조회 API
     *
     * @return 인기 검색어 탑 10 리스트
     */
    @GetMapping("/blog/popular-keywords/top10")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordService.getTop10Keywords();
    }

}
