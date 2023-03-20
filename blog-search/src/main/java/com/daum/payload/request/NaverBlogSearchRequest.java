package com.daum.payload.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NaverBlogSearchRequest {
    private final String query;
    private final String sort;
    private final Integer start;
    private final Integer display;
}

