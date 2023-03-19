package com.daum.service;

import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import reactor.core.publisher.Mono;

public interface BlogSearchService {
    Mono<KakaoBlogSearchResponse> search(KakaoBlogSearchRequest request);
}
