package com.daum.payload.request;

import com.daum.SearchApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

//@ContextConfiguration(classes = SearchApplication.class)
@SpringBootTest
public class KakaoBlogSearchRequestTest {
    @Test
    public void toNaverBlogSearchRequestTest() {
        // Given
        KakaoBlogSearchRequest kakaoRequest = KakaoBlogSearchRequest.builder()
                .query("test")
                .sort("accuracy")
                .page(2)
                .size(10)
                .build();

        // When
        NaverBlogSearchRequest naverRequest = kakaoRequest.toNaverBlogSearchRequest();

        // Then
        assertThat(naverRequest.getQuery()).isEqualTo(kakaoRequest.getQuery());
        assertThat(naverRequest.getSort()).isEqualTo("sim");
        assertThat(naverRequest.getStart()).isEqualTo(11);
        assertThat(naverRequest.getDisplay()).isEqualTo(kakaoRequest.getSize());
    }

}