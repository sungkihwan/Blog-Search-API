package com.daum.event;

import com.daum.service.PopularKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordEventListener {

    private final PopularKeywordService popularKeywordService;

    @EventListener
    public void handleKeywordUpdateEvent(BlogSearchKeywordUpdateEvent event) {
        popularKeywordService.updateKeyword(event.getKeyword());
    }
}
