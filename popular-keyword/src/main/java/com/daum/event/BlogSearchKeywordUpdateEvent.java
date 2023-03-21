package com.daum.event;

import org.springframework.context.ApplicationEvent;

public class BlogSearchKeywordUpdateEvent extends ApplicationEvent {
    private final String keyword;

    public BlogSearchKeywordUpdateEvent(Object source, String keyword) {
        super(source);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}

