package com.daum.controller;

import com.daum.SearchApplication;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

//@ContextConfiguration(classes = SearchApplication.class)
@WebFluxTest(controllers = BlogSearchController.class)
public class BlogSearchControllerTest {

//    private static final Logger logger = LoggerFactory.getLogger(BlogSearchControllerTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KakaoBlogSearchService kakaoBlogSearchService;

    @MockBean
    private PopularKeywordService popularKeywordService;

    @Test
    public void invalidRequestAllParametersExceptionHandling() {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "");
        requestData.add("sort", "invalid_sort");
        requestData.add("page", "0");
        requestData.add("size", "0");

//        // 로깅
//        Mono<String> responseBody = webTestClient.get()
//                .uri(uriBuilder -> uriBuilder.path("/api/v1/blog/search")
//                        .queryParams(requestData)
//                        .build())
//                .exchange()
//                .returnResult(String.class) // String 클래스로 변환
//                .getResponseBody()
//                .single(); // Mono<String> 변환
//
//        responseBody.subscribe(response -> logger.error("응답 본문: {}", response));

        // When
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/blog/search")
                        .queryParams(requestData)
                        .build())
                .exchange()
                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[?(@ =~ /^query.*/)]").isEqualTo("query: 검색어는 필수입니다.")
                .jsonPath("$.errors[?(@ =~ /^sort.*/)]").isEqualTo("sort: 정렬 방식은 accuracy 또는 recency 중 하나를 선택해야 합니다.")
                .jsonPath("$.errors[?(@ =~ /^page.*/)]").isEqualTo("page: 페이지 번호는 1 이상이어야 합니다.")
                .jsonPath("$.errors[?(@ =~ /^size.*/)]").isEqualTo("size: 페이지 크기는 1 이상이어야 합니다.");

    }

    @Test
    public void invalidRequestPageAndSizeParametersExceptionHandling() {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "ㅎㅎ");
        requestData.add("sort", "accuracy");
        requestData.add("page", "51");
        requestData.add("size", "51");

        // When
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/blog/search")
                        .queryParams(requestData)
                        .build())
                .exchange()
                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[?(@ =~ /^page.*/)]").isEqualTo("page: 페이지 번호는 50 이하여야 합니다.")
                .jsonPath("$.errors[?(@ =~ /^size.*/)]").isEqualTo("size: 페이지 크기는 50 이하여야 합니다.");

    }
}