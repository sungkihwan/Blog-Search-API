package com.daum.payload.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KakaoBlogSearchRequestTest {
    @Test
    @DisplayName("Kakao Parameters -> Naver Parameters 변환 테스트")
    public void toNaverBlogSearchRequestTest() {
        // Given
        KakaoBlogSearchRequest kakaoRequest = KakaoBlogSearchRequest.builder()
                .query("test")
                .sort("accuracy")
                .page(30)
                .size(40)
                .build();

        // When
        NaverBlogSearchRequest naverRequest = kakaoRequest.toNaverBlogSearchRequest();

        // Then
        assertThat(naverRequest.getQuery()).isEqualTo(kakaoRequest.getQuery());
        assertThat(naverRequest.getSort()).isEqualTo("sim");
        assertThat(naverRequest.getStart()).isEqualTo(1000);
        assertThat(naverRequest.getDisplay()).isEqualTo(kakaoRequest.getSize());
    }

}