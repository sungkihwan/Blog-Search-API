package com.daum.search.payload.request;

import lombok.Getter;

@Getter
public class NaverBlogSearchRequest {
    private final String query;
    private final String sort;
    private final Integer start;
    private final Integer display;

    public NaverBlogSearchRequest(String query, String sort, int start, int display) {
        this.query = query;
        this.sort = sort;
        this.start = start;
        this.display = display;
    }
}

