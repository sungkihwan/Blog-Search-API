package com.daum.controller;

import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.service.KakaoBlogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blog")
public class BlogSearchController {

    private final KakaoBlogSearchService kakaoBlogSearchService;

    @GetMapping("/search")
    public Mono<KakaoBlogSearchResponse> searchBlog(
            @Valid KakaoBlogSearchRequest request
    ) {
//        popularKeywordService.updateKeywordCount(request.getQuery());
        return kakaoBlogSearchService.search(request);
    }

}
