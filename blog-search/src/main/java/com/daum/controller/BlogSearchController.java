package com.daum.controller;

import com.daum.entity.PopularKeyword;
import com.daum.event.BlogSearchKeywordUpdateEvent;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BlogSearchController {

    private final KakaoBlogSearchService kakaoBlogSearchService;

    private final PopularKeywordService popularKeywordService;

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/search/blog")
    public Mono<KakaoBlogSearchResponse> searchBlog(
            @Valid KakaoBlogSearchRequest request
    ) {
        // popularKeywordService.updateKeyword(request.getQuery()); 동기식
        // 비동기식 pub - sub -> Message Broker 전환
        eventPublisher.publishEvent(new BlogSearchKeywordUpdateEvent(this, request.getQuery()));
        return kakaoBlogSearchService.search(request);
    }

    @GetMapping("/blog/popular-keywords/top10")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordService.getTop10Keywords();
    }

}
