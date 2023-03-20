package com.daum.service;

import com.daum.entity.PopularKeyword;
import com.daum.repository.PopularKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PopularKeywordServiceTest {

    @Autowired
    private PopularKeywordService popularKeywordService;

    @Autowired
    private PopularKeywordRepository popularKeywordRepository;

    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        executorService = Executors.newFixedThreadPool(20);
    }

    @Test
    public void updateKeywordCountAsyncTest() {
        String keyword1 = "간장게장";
        String keyword2 = "복숭아";
        String keyword3 = "고구마";
        String keyword4 = "라면";

        int numberOfCalls = 20;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfCalls; i++) {
            CompletableFuture.runAsync(() -> popularKeywordService.insertKeyword(keyword1), executorService);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        PopularKeyword popularKeyword = popularKeywordService.findByKeyword(keyword1);
        assertThat(popularKeyword).isNotNull();
        assertThat(popularKeyword.getCount()).isEqualTo(20);

        // 테스트 검증
//        for (String keyword : keywords) {
//            PopularKeyword popularKeyword = popularKeywordService.findByKeyword(keyword);
//            assertThat(popularKeyword).isNotNull();
//            assertThat(popularKeyword.getCount()).isEqualTo(25);
//        }
    }

}
