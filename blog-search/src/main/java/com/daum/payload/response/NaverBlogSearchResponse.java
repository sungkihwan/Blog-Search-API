package com.daum.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverBlogSearchResponse {

    private Integer total;
    private Integer start;
    private Integer display;

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

    public KakaoBlogSearchResponse toKakaoBlogSearchResponse() {
        List<KakaoBlogSearchResponse.Document> documents = this.items.stream()
                .map(NaverBlogSearchResponse::convertDocument)
                .collect(Collectors.toList());

        boolean isEnd = this.start + this.display >= this.total;
        KakaoBlogSearchResponse.Meta meta = KakaoBlogSearchResponse.Meta.builder()
                .totalCount(this.total)
                .pageableCount(this.total)
                .isEnd(isEnd)
                .build();

        return KakaoBlogSearchResponse.builder()
                .meta(meta)
                .documents(documents)
                .build();
    }

    private static KakaoBlogSearchResponse.Document convertDocument(NaverBlogSearchItem naverItem) {
        String title = naverItem.getTitle();
        String contents = naverItem.getDescription();
        String url = naverItem.getLink();
        String blogName = naverItem.getBloggername();
        String thumbnail = "";
        String dateTime = naverItem.getPostdate();

        return KakaoBlogSearchResponse.Document.builder()
                .title(title)
                .contents(contents)
                .url(url)
                .blogName(blogName)
                .thumbnail(thumbnail)
                .dateTime(dateTime)
                .build();
    }

}
