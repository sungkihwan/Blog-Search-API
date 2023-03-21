package com.daum.controller;

import com.daum.payload.response.KakaoBlogSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class BlogControllerMockMvcTest {

    private static final Logger logger = LoggerFactory.getLogger(BlogControllerMockMvcTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("MockMvc 잘못된 요청 매개 변수 처리 테스트 (BindException)")
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
    @DisplayName("컨트롤러 성공 검증")
    public void validRequestSuccessHandling() throws Exception {
        // Given
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("query", "고구마");
        requestData.add("sort", "recency");
        requestData.add("page", "1");
        requestData.add("size", "10");

        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/search/blog")
                        .queryParams(requestData)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoBlogSearchResponse response = objectMapper.readValue(responseBody, KakaoBlogSearchResponse.class);

        assertThat(response.getDocuments().size()).isEqualTo(10);
    }
}

