package com.daum.search.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverBlogSearchResponse {

//    @JsonProperty("items")
//    private String lastBuildDate;
    private int total;
    private int start;
    private int display;

    @JsonProperty("items")
    private List<NaverBlogSearchItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverBlogSearchItem {
        private String title;
        private String link;
        private String description;
        private String bloggername;
        private String bloggerlink;
        private String postdate;
    }
}
