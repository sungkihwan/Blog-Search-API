package com.daum.controller;

import com.daum.entity.PopularKeyword;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blog")
@Validated
public class BlogSearchController {

    private final KakaoBlogSearchService kakaoBlogSearchService;

    private final PopularKeywordService popularKeywordService;

    @GetMapping("/search")
    public Mono<KakaoBlogSearchResponse> searchBlog(
            @Valid KakaoBlogSearchRequest request
    ) {
        popularKeywordService.insertKeyword(request.getQuery());
        return kakaoBlogSearchService.search(request);
    }

    @GetMapping("/popular-keywords/top10")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordService.getTop10Keywords();
    }

}
