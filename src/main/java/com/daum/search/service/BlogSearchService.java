package com.daum.search.service;

import com.daum.search.payload.request.KakaoBlogSearchRequest;
import com.daum.search.payload.response.KakaoBlogSearchResponse;
import reactor.core.publisher.Mono;

public interface BlogSearchService {
    Mono<KakaoBlogSearchResponse> search(KakaoBlogSearchRequest request);
}
