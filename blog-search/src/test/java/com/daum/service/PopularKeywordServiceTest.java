package com.daum.service;

import com.daum.entity.PopularKeyword;
import com.daum.repository.PopularKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("같은 키워드 동시저장 요청 및 탑텐 리스트 검증 테스트")
    public void updateKeywordCountAsyncAndGetTop10ListTest() throws InterruptedException {
        // 테스트 데이터 준비
        String keyword1 = "간장게장";
        String keyword2 = "복숭아";
        String keyword3 = "고구마";
        String keyword4 = "라면";

        int numOfCallsOfKeyword1 = 20;
        int numOfCallsOfKeyword2 = 25;
        int numOfCallsOfKeyword3 = 40;
        int numOfCallsOfKeyword4 = 12;

        // 비동기 동시 호출 테스트 (동시성 컨트롤)
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numOfCallsOfKeyword1; i++) {
            CompletableFuture.runAsync(() -> popularKeywordService.updateKeyword(keyword1), executorService);
        }

        for (int i = 0; i < numOfCallsOfKeyword2; i++) {
            CompletableFuture.runAsync(() -> popularKeywordService.updateKeyword(keyword2), executorService);
        }

        for (int i = 0; i < numOfCallsOfKeyword3; i++) {
            CompletableFuture.runAsync(() -> popularKeywordService.updateKeyword(keyword3), executorService);
        }

        for (int i = 0; i < numOfCallsOfKeyword4; i++) {
            CompletableFuture.runAsync(() -> popularKeywordService.updateKeyword(keyword4), executorService);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // top 10 검증을 위해 추가 데이터 입력
        for (int i = 1; i <= 8; i++) {
            popularKeywordService.updateKeyword("top10검증" + i);
        }

        // 결과 검증을 위해 잠시 대기
        Thread.sleep(2000);

        // 결과 검증
        assertThat(popularKeywordService.findByKeyword(keyword1).getCount()).isEqualTo(numOfCallsOfKeyword1);
        assertThat(popularKeywordService.findByKeyword(keyword2).getCount()).isEqualTo(numOfCallsOfKeyword2);
        assertThat(popularKeywordService.findByKeyword(keyword3).getCount()).isEqualTo(numOfCallsOfKeyword3);
        assertThat(popularKeywordService.findByKeyword(keyword4).getCount()).isEqualTo(numOfCallsOfKeyword4);

        List<PopularKeyword> popularKeywordList = popularKeywordService.getTop10Keywords();
        assertThat(popularKeywordList).isNotEmpty();
        assertThat(popularKeywordList.size()).isLessThanOrEqualTo(10);

        assertThat(popularKeywordList.get(0).getCount()).isEqualTo(numOfCallsOfKeyword3);
        assertThat(popularKeywordList.get(1).getCount()).isEqualTo(numOfCallsOfKeyword2);
        assertThat(popularKeywordList.get(2).getCount()).isEqualTo(numOfCallsOfKeyword1);
        assertThat(popularKeywordList.get(3).getCount()).isEqualTo(numOfCallsOfKeyword4);

    }

}
