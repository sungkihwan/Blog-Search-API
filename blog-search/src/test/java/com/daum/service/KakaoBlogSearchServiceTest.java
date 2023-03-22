package com.daum.service;

import static org.junit.jupiter.api.Assertions.*;

import com.daum.common.exception.ExternalApiException;
import com.daum.payload.request.KakaoBlogSearchRequest;
import com.daum.payload.response.KakaoBlogSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

//@ContextConfiguration(classes = SearchApplication.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class KakaoBlogSearchServiceTest {

    @Autowired
    private KakaoBlogSearchService kakaoBlogSearchService;

    @Autowired
    private NaverBlogSearchService naverBlogSearchService;

    private KakaoBlogSearchService kakaoBlogSearchServiceNaverFallback;

    private KakaoBlogSearchService kakaoBlogSearchServiceUnauthorized;

    @BeforeEach
    public void setup() {
        kakaoBlogSearchServiceNaverFallback = new KakaoBlogSearchService(null, naverBlogSearchService);
        NaverBlogSearchService naverBlogSearchServiceUnauthorized = new NaverBlogSearchService(null, null);
        kakaoBlogSearchServiceUnauthorized = new KakaoBlogSearchService(null, naverBlogSearchServiceUnauthorized);
    }

    @Test
    @DisplayName("Blog 검색하기 성공")
    public void kakaoSearchSuccess() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("ㅂㅁㄷ테스트")
                .sort("recency")
                .build();
        // When
        Mono<KakaoBlogSearchResponse> result = kakaoBlogSearchService.search(request);

        // Then
        StepVerifier.create(result)
                .assertNext(res -> {
                    assertNotNull(res.getMeta());
                    assertNotNull(res.getDocuments());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Blog 검색하기 모든 파라미터 성공")
    public void KakaoSearchAllParametersSuccess() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("ㅂㅁㄷ테스트")
                .sort("accuracy")
                .size(1)
                .page(1)
                .build();
        // When
        Mono<KakaoBlogSearchResponse> result = kakaoBlogSearchService.search(request);

        // Then
        StepVerifier.create(result)
                .assertNext(res -> {
                    assertNotNull(res.getMeta());
                    assertNotNull(res.getDocuments());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Blog 검색 Kakao, Naver Key 없는 경우")
    public void unauthorizedSearchRequest() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("ㅂㅁㄷ테스트")
                .build();

        // When
        Mono<KakaoBlogSearchResponse> result = kakaoBlogSearchServiceUnauthorized.search(request);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ExternalApiException
                        && ((ExternalApiException) throwable).getHttpStatus() == HttpStatus.UNAUTHORIZED)
                .verify();
    }

    @Test
    @DisplayName("Kakao Error시 Naver 호출 테스트")
    public void kakaoSearchToNaverFallbackTest() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("간장게장")
                .size(1)
                .build();

        // When
        // Naver API 호출 결과를 받아옵니다.
        Mono<KakaoBlogSearchResponse> naverResponse = naverBlogSearchService.search(request);

        // Kakao API 호출 시 (key=null) 401에러로 Naver API 호출 결과를 받아옵니다.
        Mono<KakaoBlogSearchResponse> kakaoResponse = kakaoBlogSearchServiceNaverFallback.search(request);

        StepVerifier.create(kakaoResponse)
                .assertNext(res -> {
                    assertNotNull(res.getMeta());
                    assertNotNull(res.getDocuments());
                })
                .verifyComplete();

        // Then
        Mono<Boolean> isEqual = kakaoResponse.zipWith(naverResponse, Objects::equals);

        StepVerifier.create(isEqual)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Kakao 블로그 검색 배드 리퀘스트")
    public void kakaoSearchBadRequest() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("테스트")
                .size(1000)
                .page(100)
                .build();
        // When
        // Kakao API 호출 시 400 에러 -> Naver API 호출 시 400 에러
        Mono<KakaoBlogSearchResponse> result = kakaoBlogSearchService.search(request);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ExternalApiException
                        && ((ExternalApiException) throwable).getHttpStatus() == HttpStatus.BAD_REQUEST)
                .verify();
    }

    @Test
    @DisplayName("Naver 블로그 검색 기능 성공")
    public void successNaverSearchProcessing() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("테스트")
                .size(1)
                .build();

        // When
        Mono<KakaoBlogSearchResponse> result = naverBlogSearchService.search(request);

        // Then
        StepVerifier.create(result)
                .assertNext(res -> {
                    assertNotNull(res.getMeta());
                    assertNotNull(res.getDocuments());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("검색 결과가 비어있는 경우")
    public void searchEmptyResult() {
        // Given
        KakaoBlogSearchRequest request = KakaoBlogSearchRequest
                .builder()
                .query("ㅁㄹㄴㄹㄴㄱㄴㅇㅁㄴㅇㅁㄴㅇㅁㄴㅇㅁㄹㄹㄷㄱㄹㄴㅁㄹㄴㅁㅇㄴㅁ")
                .build();

        // When
        Mono<KakaoBlogSearchResponse> result = kakaoBlogSearchService.search(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getMeta().getTotalCount() == 0)
                .verifyComplete();
    }

}