package com.daum.search.controller;

import com.daum.search.entity.PopularKeyword;
import com.daum.search.payload.request.KakaoBlogSearchRequest;
import com.daum.search.payload.response.KakaoBlogSearchResponse;
import com.daum.search.service.KakaoBlogSearchService;
import com.daum.search.service.NaverBlogSearchService;
import com.daum.search.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blog")
public class BlogSearchController {

    private final KakaoBlogSearchService kakaoBlogSearchService;

    private final PopularKeywordService popularKeywordService;

    @GetMapping("/search")
    public Mono<KakaoBlogSearchResponse> searchBlog(
            @Valid KakaoBlogSearchRequest request
    ) {
        popularKeywordService.updateKeywordCount(request.getQuery());
        return kakaoBlogSearchService.search(request);
    }

    @GetMapping("/popular-keywords/top10")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordService.getTop10Keywords();
    }
}
