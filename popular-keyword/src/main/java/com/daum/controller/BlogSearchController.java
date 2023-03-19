package com.daum.controller;

import com.daum.entity.PopularKeyword;
import com.daum.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blog")
public class BlogSearchController {

    private final PopularKeywordService popularKeywordService;

    @GetMapping("/popular-keywords/top10")
    public List<PopularKeyword> getTop10Keywords() {
        return popularKeywordService.getTop10Keywords();
    }
}
