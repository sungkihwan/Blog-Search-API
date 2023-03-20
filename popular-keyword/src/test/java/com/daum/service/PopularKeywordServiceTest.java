package com.daum.service;

import com.daum.entity.PopularKeyword;
import com.daum.repository.PopularKeywordRepository;
import com.daum.service.PopularKeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PopularKeywordServiceTest {

//    @InjectMocks
//    private PopularKeywordService popularKeywordService;
//
//    private PopularKeywordServiceTestFixture popularKeywordServiceTestFixture;
//
//    @Mock
//    private PopularKeywordRepository popularKeywordRepository;
//
//    private ExecutorService executorService;
//
//    @BeforeEach
//    public void setUp() {
//        executorService = Executors.newFixedThreadPool(20);
//        ReflectionTestUtils.setField(popularKeywordService, "executorService", executorService);
//    }
//
//    @Test
//    public void getTop10KeywordsTest() {
//        // given
//        List<PopularKeyword> mockKeywords = popularKeywordServiceTestFixture.getMockKeywords();
//
//        when(popularKeywordRepository.findTop10ByOrderByCountDesc()).thenReturn(mockKeywords);
//
//        // when
//        List<PopularKeyword> result = popularKeywordService.getTop10Keywords();
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(result.get(0).getKeyword()).isEqualTo("간장게장");
//        assertThat(result.get(0).getCount()).isEqualTo(10L);
//        assertThat(result.get(1).getKeyword()).isEqualTo("롯데월드");
//        assertThat(result.get(1).getCount()).isEqualTo(9L);
//    }
//
//    @Test
//    public void updateKeywordCountTest() throws Exception {
//        // given
//        String keyword = "테스트";
//        when(popularKeywordRepository.updateKeywordCount(keyword)).thenReturn(0);
//        when(popularKeywordRepository.save(any(PopularKeyword.class))).thenReturn(new PopularKeyword(keyword, 1L));
//
//        // when
//        List<CompletableFuture<Void>> futures = Arrays.asList(
//                CompletableFuture.runAsync(() -> popularKeywordService.updateKeywordCount(keyword), executorService),
//                CompletableFuture.runAsync(() -> popularKeywordService.updateKeywordCount(keyword), executorService),
//                CompletableFuture.runAsync(() -> popularKeywordService.updateKeywordCount(keyword), executorService),
//                CompletableFuture.runAsync(() -> popularKeywordService.updateKeywordCount(keyword), executorService),
//                CompletableFuture.runAsync(() -> popularKeywordService.updateKeywordCount(keyword), executorService)
//        );
//
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//        // then
//        verify(popularKeywordRepository, times(5)).updateKeywordCount(keyword);
//        verify(popularKeywordRepository, atLeastOnce()).save(any(PopularKeyword.class));
//    }
}
