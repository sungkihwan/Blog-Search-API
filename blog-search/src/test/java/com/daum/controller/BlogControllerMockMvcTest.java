package com.daum.controller;

import com.daum.entity.PopularKeyword;
import com.daum.service.KakaoBlogSearchService;
import com.daum.service.PopularKeywordService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class BlogControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoBlogSearchService kakaoBlogSearchService;

    @MockBean
    private PopularKeywordService popularKeywordService;

    @Test
    @DisplayName("블로그 검색 잘못된 리퀘스트 (BindException)")
    void invalidRequestExceptionHandling() throws Exception {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "");
        requestData.add("sort", "invalid_sort");
        requestData.add("page", "0");
        requestData.add("size", "0");

        // When & Then
        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/search/blog")
                        .queryParams(requestData)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        byte[] responseBodyBytes = response.getContentAsByteArray();
        String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(responseBody);

        JSONArray errors = jsonObject.getJSONArray("errors");

        boolean sortErrorMessageFound = false;
        boolean sizeErrorMessageFound = false;
        boolean queryErrorMessageFound = false;
        boolean pageErrorMessageFound = false;

        for (int i = 0; i < errors.length(); i++) {
            String errorMessage = errors.getString(i);

            if (errorMessage.equals("sort: 정렬 방식은 accuracy 또는 recency 중 하나를 선택해야 합니다.")) {
                sortErrorMessageFound = true;
            }

            if (errorMessage.equals("size: 페이지 크기는 1 이상이어야 합니다.")) {
                sizeErrorMessageFound = true;
            }

            if (errorMessage.equals("query: 검색어는 필수입니다.")) {
                queryErrorMessageFound = true;
            }

            if (errorMessage.equals("page: 페이지 번호는 1 이상이어야 합니다.")) {
                pageErrorMessageFound = true;
            }
        }

        assertThat(sortErrorMessageFound).isTrue();
        assertThat(sizeErrorMessageFound).isTrue();
        assertThat(queryErrorMessageFound).isTrue();
        assertThat(pageErrorMessageFound).isTrue();
    }

    @Test
    @DisplayName("블로그 검색 컨트롤러 성공")
    public void validRequestSuccessHandling() throws Exception {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "고구마");
        requestData.add("sort", "recency");
        requestData.add("page", "1");
        requestData.add("size", "10");

        // When
        mockMvc.perform(get("/api/v1/search/blog")
                        .queryParams(requestData)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("탑텐 키워드 리스트 성공")
    public void getTop10KeywordsWithSuccess() throws Exception {
        // Given
        List<PopularKeyword> popularKeywords = Arrays.asList(
                new PopularKeyword("키워드1", 10L),
                new PopularKeyword("키워드2", 9L),
                new PopularKeyword("키워드3", 8L)
        );

        when(popularKeywordService.getTop10Keywords()).thenReturn(popularKeywords);

        // When & Then
        mockMvc.perform(get("/api/v1/blog/popular-keywords/top10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].keyword").value("키워드1"))
                .andExpect(jsonPath("$[0].count").value(10))
                .andExpect(jsonPath("$[1].keyword").value("키워드2"))
                .andExpect(jsonPath("$[1].count").value(9))
                .andExpect(jsonPath("$[2].keyword").value("키워드3"))
                .andExpect(jsonPath("$[2].count").value(8));
    }

    @Test
    @DisplayName("탑텐 키워드 리스트 에러")
    public void getTop10KeywordsWithError() throws Exception {
        // Given
        when(popularKeywordService.getTop10Keywords()).thenThrow(new RuntimeException("서비스 에러"));

        // When & Then
        mockMvc.perform(get("/api/v1/blog/popular-keywords/top10"))
                .andExpect(status().isInternalServerError());
    }
}

