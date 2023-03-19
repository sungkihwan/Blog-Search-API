package com.daum.search.service;

import com.daum.search.common.logging.LoggingFilter;
import com.daum.search.payload.request.KakaoBlogSearchRequest;
import com.daum.search.payload.request.NaverBlogSearchRequest;
import com.daum.search.payload.response.KakaoBlogSearchResponse;
import com.daum.search.payload.response.NaverBlogSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.daum.search.common.constant.Constants.*;

@Service
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
                .map(NaverBlogSearchService::convert);
    }

    private Mono<NaverBlogSearchResponse> searchWithNaver(NaverBlogSearchRequest request) {
        return naverBlogSearchWebClient.get()
                .uri(uriBuilder -> buildNaverUrI(request))
                .retrieve()
                .bodyToMono(NaverBlogSearchResponse.class)
                .retry(maxRetry);
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

    private static KakaoBlogSearchResponse convert(NaverBlogSearchResponse naverResponse) {
        List<KakaoBlogSearchResponse.Document> documents = naverResponse.getItems().stream()
                .map(NaverBlogSearchService::convertDocument)
                .collect(Collectors.toList());

        int totalCount = naverResponse.getTotal();
        boolean isEnd = naverResponse.getStart() + naverResponse.getDisplay() >= totalCount;
        KakaoBlogSearchResponse.Meta meta = new KakaoBlogSearchResponse.Meta(totalCount, totalCount, isEnd);

        return new KakaoBlogSearchResponse(meta, documents);
    }

    private static KakaoBlogSearchResponse.Document convertDocument(NaverBlogSearchResponse.NaverBlogSearchItem naverItem) {
        String title = naverItem.getTitle();
        String contents = naverItem.getDescription();
        String url = naverItem.getLink();
        String blogName = naverItem.getBloggername();
        String thumbnail = ""; // 썸네일 정보가 없으므로 빈 문자열로 설정
        String dateTime = naverItem.getPostdate();

        return new KakaoBlogSearchResponse.Document(title, contents, url, blogName, thumbnail, dateTime);
    }
}
