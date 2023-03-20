package com.daum.payload.response;

import com.daum.SearchApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@ContextConfiguration(classes = SearchApplication.class)
@SpringBootTest
public class NaverBlogSearchResponseTest {

    @Test
    public void toKakaoBlogSearchResponseTest() {
        // Given
        NaverBlogSearchResponse naverResponse = new NaverBlogSearchResponse();
        naverResponse.setTotal(50);
        naverResponse.setStart(40);
        naverResponse.setDisplay(10);

        List<NaverBlogSearchResponse.NaverBlogSearchItem> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String title = "title" + i;
            String link = "link" + i;
            String description = "description" + i;
            String bloggerName = "blogger" + i;
            String postdate = "20220101";
            NaverBlogSearchResponse.NaverBlogSearchItem item = new NaverBlogSearchResponse.NaverBlogSearchItem(title, link, description, bloggerName, "", postdate);
            items.add(item);
        }
        naverResponse.setItems(items);

        // When
        KakaoBlogSearchResponse kakaoResponse = naverResponse.toKakaoBlogSearchResponse();

        // Then
        assertEquals(50, kakaoResponse.getMeta().getTotalCount());
        assertEquals(true, kakaoResponse.getMeta().getIsEnd());
        assertEquals(10, kakaoResponse.getDocuments().size());
        for (int i = 0; i < 10; i++) {
            assertEquals("title" + (i+1), kakaoResponse.getDocuments().get(i).getTitle());
            assertEquals("link" + (i+1), kakaoResponse.getDocuments().get(i).getUrl());
            assertEquals("description" + (i+1), kakaoResponse.getDocuments().get(i).getContents());
            assertEquals("blogger" + (i+1), kakaoResponse.getDocuments().get(i).getBlogName());
            assertEquals("", kakaoResponse.getDocuments().get(i).getThumbnail());
            assertEquals("20220101", kakaoResponse.getDocuments().get(i).getDateTime());
        }
    }

}