package com.daum.service;

import com.daum.common.exception.ExternalApiException;
import com.daum.common.logging.LoggingFilter;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
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

import static com.daum.common.constant.Constants.KAKAO_BLOG_SEARCH_URL;
import static com.daum.common.constant.Constants.maxRetry;


@Service
@Slf4j
public class KakaoBlogSearchService implements BlogSearchService {

    private final WebClient kakaoBlogSearchWebClient;

    private final NaverBlogSearchService naverBlogSearchService;

    public KakaoBlogSearchService(
            @Value("${kakao.search.rest-api-key}") String kakaoRestApiKey,
            NaverBlogSearchService naverBlogSearchService
    ) {
        this.naverBlogSearchService = naverBlogSearchService;
        this.kakaoBlogSearchWebClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "KakaoAK " + kakaoRestApiKey)
                .filter(LoggingFilter.logRequest())
                .filter(LoggingFilter.logResponse())
                .build();
    }

    @Override
    public Mono<KakaoBlogSearchResponse> search(KakaoBlogSearchRequest request) {
        return searchWithKakao(request)
                .onErrorResume(e ->
                        naverBlogSearchService.search(request)
                )
                .onErrorResume(e -> Mono.error(new ExternalApiException("블로그 검색 API 에러가 발생했습니다.", e)));
    }

    private Mono<KakaoBlogSearchResponse> searchWithKakao(KakaoBlogSearchRequest request) {
        return kakaoBlogSearchWebClient.get()
                .uri(uriBuilder -> buildKakaoUrI(request))
                .retrieve()
                .bodyToMono(KakaoBlogSearchResponse.class)
                .retry(maxRetry)
                .doOnError(eK ->
                        log.error("카카오 블로그 검색 API 호출에서 에러 발생: 상태코드={}, 메시지={}",
                                ((WebClientResponseException) eK).getStatusCode(),
                                eK.getMessage())
                );
    }

    private URI buildKakaoUrI(KakaoBlogSearchRequest request) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(KAKAO_BLOG_SEARCH_URL)
                .queryParam("query", request.getQuery());

        if (request.getSort() != null && !request.getSort().isEmpty()) {
            uriComponentsBuilder.queryParam("sort", request.getSort());
        }

        if (request.getPage() != null && request.getPage() > 0) {
            uriComponentsBuilder.queryParam("page", request.getPage());
        }

        if (request.getSize() != null && request.getSize() > 0) {
            uriComponentsBuilder.queryParam("size", request.getSize());
        }

        return uriComponentsBuilder.encode().build().toUri();
    }

}
