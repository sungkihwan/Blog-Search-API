package com.daum.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoBlogSearchResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("documents")
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("pageable_count")
        private Integer pageableCount;

        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Document {
        // 블로그 글 제목
        private String title;
        // 블로그 글 요약
        private String contents;
        // 블로그 글 URL
        private String url;
        // 블로그의 이름
        @JsonProperty("blogname")
        private String blogName;
        // 썸네일 이미지 URL
        private String thumbnail;
        // 블로그 글 작성시간
        @JsonProperty("datetime")
        private String dateTime;
    }
}
