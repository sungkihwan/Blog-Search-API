package com.daum.payload.request;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class KakaoBlogSearchRequest {

    @NotBlank(message = "검색어는 필수입니다.")
    private String query;

    @Pattern(regexp = "accuracy|recency", message = "정렬 방식은 accuracy 또는 recency 중 하나를 선택해야 합니다.")
    private String sort;

    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    @Max(value = 50, message = "페이지 번호는 50 이하여야 합니다.")
    private Integer page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
    private Integer size;

    public NaverBlogSearchRequest toNaverBlogSearchRequest() {
        return new NaverBlogSearchRequest(
                this.query,
                convertSort(this.sort),
                convertStart(this.page, this.size),
                this.size
        );
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

