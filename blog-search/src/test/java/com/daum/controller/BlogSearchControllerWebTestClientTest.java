package com.daum.controller;

import com.daum.payload.response.KakaoBlogSearchResponse;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//@ContextConfiguration(classes = SearchApplication.class)
@WebFluxTest(controllers = BlogSearchController.class)
public class BlogSearchControllerWebTestClientTest {

//    private static final Logger logger = LoggerFactory.getLogger(BlogSearchControllerTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KakaoBlogSearchService kakaoBlogSearchService;

    @MockBean
    private PopularKeywordService popularKeywordService;

    @Test
    @DisplayName("WebClient 잘못된 요청 매개 변수 처리 테스트 (WebExchangeBindException)")
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
//        responseBody.subscribe(response -> logger.error("본문: {}", response));

        // When
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/search/blog")
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
    @DisplayName("WebClient 일부 파라미터 잘못된 요청 처리 테스트")
    public void invalidRequestPageAndSizeParametersExceptionHandling() {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "ㅎㅎ");
        requestData.add("sort", "accuracy");
        requestData.add("page", "51");
        requestData.add("size", "51");

        // When
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/search/blog")
                        .queryParams(requestData)
                        .build())
                .exchange()
                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[?(@ =~ /^page.*/)]").isEqualTo("page: 페이지 번호는 50 이하여야 합니다.")
                .jsonPath("$.errors[?(@ =~ /^size.*/)]").isEqualTo("size: 페이지 크기는 50 이하여야 합니다.");

    }

    @Test
    @DisplayName("컨트롤러 성공 검증")
    public void validRequestSuccessHandling() {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "고구마");
        requestData.add("sort", "recency");
        requestData.add("page", "1");
        requestData.add("size", "10");

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/search/blog")
                        .queryParams(requestData)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(KakaoBlogSearchResponse.class)
                .value(response -> assertThat(response.getDocuments().size()).isEqualTo(10));
    }
}