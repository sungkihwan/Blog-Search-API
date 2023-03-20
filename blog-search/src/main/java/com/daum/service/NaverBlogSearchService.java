package com.daum.service;

import com.daum.common.logging.LoggingFilter;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.request.NaverBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.payload.response.NaverBlogSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.daum.common.constant.Constants.NAVER_BLOG_SEARCH_URL;
import static com.daum.common.constant.Constants.maxRetry;


@Service
@Slf4j
public class NaverBlogSearchService implements BlogSearchService {

    private final WebClient naverBlogSearchWebClient;

    public NaverBlogSearchService(
            @Value("${naver.search.client-id}") String naverClientId,
            @Value("${naver.search.client-secret}") String naverClientSecret
    ) {
        this.naverBlogSearchWebClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", naverClientId)
                .defaultHeader("X-Naver-Client-Secret", naverClientSecret)
                .filter(LoggingFilter.logRequest())
                .filter(LoggingFilter.logResponse())
                .build();
    }

    @Override
    public Mono<KakaoBlogSearchResponse> search(KakaoBlogSearchRequest request) {
        return searchWithNaver(request.toNaverBlogSearchRequest())
                .map(NaverBlogSearchResponse::toKakaoBlogSearchResponse);
    }

    private Mono<NaverBlogSearchResponse> searchWithNaver(NaverBlogSearchRequest request) {
        return naverBlogSearchWebClient.get()
                .uri(uriBuilder -> buildNaverUrI(request))
                .retrieve()
                .bodyToMono(NaverBlogSearchResponse.class)
                .retry(maxRetry)
                .doOnError(eN ->
                        log.error("네이버 블로그 검색 API 호출에서 에러 발생: 상태코드={}, 메시지={}",
                                ((WebClientResponseException) eN).getStatusCode(),
                                eN.getMessage())
                );
    }

    private URI buildNaverUrI(NaverBlogSearchRequest request) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(NAVER_BLOG_SEARCH_URL)
                .queryParam("query", request.getQuery());

        if (request.getSort() != null && !request.getSort().isEmpty()) {
            uriComponentsBuilder.queryParam("sort", request.getSort());
        }

        if (request.getStart() != null && request.getStart() > 0) {
            uriComponentsBuilder.queryParam("start", request.getStart());
        }

        if (request.getDisplay() != null && request.getDisplay() > 0) {
            uriComponentsBuilder.queryParam("display", request.getDisplay());
        }

        return uriComponentsBuilder.encode().build().toUri();
    }
}
