package com.daum.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoBlogSearchRequest {

    @NotBlank(message = "query: 검색어는 필수입니다.")
    private String query;

    @Pattern(regexp = "accuracy|recency", message = "sort: 정렬 방식은 accuracy 또는 recency 중 하나를 선택해야 합니다.")
    private String sort;

    @Min(value = 1, message = "page: 페이지 번호는 1 이상이어야 합니다.")
    @Max(value = 50, message = "page: 페이지 번호는 50 이하여야 합니다.")
    private Integer page;

    @Min(value = 1, message = "size: 페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 50, message = "size: 페이지 크기는 50 이하여야 합니다.")
    private Integer size;

    public NaverBlogSearchRequest toNaverBlogSearchRequest() {
        return NaverBlogSearchRequest.builder()
                .query(this.query)
                .sort(convertSort(this.sort))
                .start(convertStart(this.page, this.size))
                .display(this.size)
                .build();
    }

    private String convertSort(String kakaoSort) {
        if ("accuracy".equals(kakaoSort)) {
            return "sim";
        } else if ("recency".equals(kakaoSort)) {
            return "date";
        } else {
            return null;
        }
    }

    private Integer convertStart(Integer page, Integer size) {
        if (page != null && size != null) {
            return (page - 1) * size + 1;
        } else {
            return null;
        }
    }
}

