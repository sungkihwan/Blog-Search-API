# 애플리케이션 실행 방법

### 다운로드링크 [https://github.com/sungkihwan/Blog-Search-API/raw/main/blog-search-0.0.1-SNAPSHOT.jar](https://github.com/sungkihwan/Blog-Search-API/raw/main/blog-search-0.0.1-SNAPSHOT.jar)

cd 다운로드 폴더

### java -jar blog-search-0.0.1-SNAPSHOT.jar

- 서버 포트는 7712로 실행됩니다.

# 서비스 소개

**블로그 검색** 및 **인기있는 검색어를 리스트업** 하는 서비스입니다.

# 서비스 요구사항

### 블로그 검색

특정 키워드를 통해 블로그를 검색할 수 있어야 합니다.

검색 결과에서 ‘정확도순’으로 Sorting 기능을 지원해야 합니다.

검색 결과에서 ‘최신순’으로 Sorting 기능을 지원해야 합니다.

검색 결과는 Pagination 형태로 제공해야 합니다.

검색 소스는 카카오 API의 키워드로 [블로그 검색](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog)을 활용해야 합니다.

추후 카카오 API 이외에 새로운 검색 소스가 추가될 수 있음을 고려해서 설계해야 합니다.

### 인기 검색어 목록

사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공해야 합니다.

검색어 별로 검색된 횟수가 표시되어야 합니다.

# 사용된 기술 스택 소개

| 구분 | 기술 |
| --- | --- |
| Language | Java 11 |
| DB | h2 |
| 외부 Open API | Naver Open API, Kakao Open API |
| Framework | Spring Boot 2.7.9 |
| Dependency | Gradle 7.6.1 |

## 사용된 Library 및 사용 목적

| Library 명 | 사용 목적 |
| --- | --- |
| Jpa | DB 컨트롤 |
| spring-boot-starter-validation | 컨트롤러에서 파라미터의 검증을 위해서 사용 |
| spring-boot-starter-webflux | Webclient를 통해 외부 API (Kakao Open API) 비동기 호출 |
| io.projectreactor:reactor-test:3.4.13 | Webclient 테스트를 위해 사용 |
| Lombok | Getter, Setter, 생성자 등의 편리한 코드작업 |
| Junit5 | 테스트 코드 작성 |

# 프로젝트 구조
<img width="426" alt="스크린샷 2023-03-21 오후 11 06 40" src="https://user-images.githubusercontent.com/61499337/226783151-66a693c2-d6bb-4143-a6f6-457703c40a14.png">

1. blog-search, popular-keyword 멀티 모듈로 구성되어 있습니다.
2. 인스턴스로 띄워지는 모듈은 blog-search 하나입니다.
3. popular-keyword는 인기 검색어 DB 컨트롤을 위한 컨피그, 엔티티, 서비스 패키지로 구성되어 있습니다.
4. blog-search는 단일 endpoint로 블로그 검색, 인기검색어 조회 가능합니다.
5. blog-search는 popular-keyword를 모듈 임포트하여 사용합니다. (검색 시 인기 검색어 카운트 + 1 이벤트 발행)
6. 디렉토리 구조가 이런 이유는 popular-keyword가 추후에 msa 형태로 실행될 수 있도록 구성했습니다.

# API 명세

## 1. 블로그 검색

<aside>
💡 특정 키워드로 블로그를 검색합니다. <br>
1. 키워드를 DB에 저장하며 검색 카운트를 + 1 증가시키는 Event를 발행합니다. <br>
2. Kakao Open API를 사용하여 블로그 데이터를 받아옵니다. <br>

- 키워드를 + 1 증가할때는 k8s pod 처럼 다수의 애플리케이션으로 구동되더라도 문제가 없도록 구현되어 있습니다.
(db의 부하는 신경쓰지 않았기에 트래픽이 늘면 파티셔닝이나 배압조절이 필요합니다.) <br>
- 기본적으로 Kakao Open API를 사용하여 데이터를 받아오지만 카카오 API가 장애시 네이버 API를 사용하여 호출합니다.

</aside>

### `http://localhost:7712/api/v1/search/blog?query={query}&size={size}&page={page}&sort={sort}`

- HttpMethod
    - Get
- Request
    
    ex) http://localhost:7712/api/v1/search/blog?query=간장게장&size=1&page=3&sort=accuracy
    <img width="808" alt="스크린샷 2023-03-21 오후 11 33 46" src="https://user-images.githubusercontent.com/61499337/226783383-7a14c7bd-c174-49a9-958f-7f5dee20de82.png">
    
- HttpStatus
    - Success : 200
    - 잘못된 파라미터 요청(Bad Request) : 400  ex :`/api/v1/search/blog?size=100&page=3&sort=accuracy`
    - 카카오, 네이버 API 에러 : 4xx
    - 서버 에러 : 500
- Response
    
    ```json
    {
        "meta": {
            "total_count": 958833,
            "pageable_count": 800,
            "is_end": false
        },
        "documents": [
            {
                "title": "[인천] 0티어 <b>간장</b><b>게장</b> 맛집, 송림동 &#39;삼대인천<b>게장</b>&#39;",
                "contents": "전국 <b>간장</b><b>게장</b> 지도를 그릴 기세로 비교를 하고 있는데요. 저도 굳이 왜 이렇게까지 하는건지 모르겠지만 ㅋㅋㅋ",
                "url": "http://plusxreviews.tistory.com/72",
                "thumbnail": "https://search4.kakaocdn.net/argon/130x130_85_c/5HUUEyJdEnL",
                "blogname": "플러스엑스_리뷰",
                "datetime": "2023-03-14T08:20:10.000+09:00"
            }
        ]
    }
    ```
    

## 2. 인기 검색어 TOP10 리스트 조회

<aside>
💡 검색된 키워드의 탑 10 리스트 내림차순으로 불러옵니다. <br>
1. 리스트는 현재 5초마다 동기화 됩니다. (5초마다 캐시 무효화) 즉 검색을 하더라도 5초 뒤에 적용이 됩니다. <br>
2. count는 인덱스가 적용되어 빠르게 조회할 수 있지만 캐싱되어있는 결과보다는 물리적으로 느리기에 캐싱을 적용했습니다. <br>
3. 현재 요구사항이 명확하지 않아서 얼마나 실시간성을 요구하는지 모르기에 상황에 따라 다르게 변경해야 합니다. ex) batch & stream

</aside>

### http://localhost:7712/api/v1/blog/popular-keywords/top10

- HttpMethod
    - Get
- HttpStatus
    - Success : 200
    - 서버 에러 : 500
- Response
    
    ```json
    [
        {
            "keyword": "간장게장이 맛있음",
            "count": 10
        },
    		{
            "keyword": "고구마도",
            "count": 5
        },
    		{
            "keyword": "간장게장2",
            "count": 3
        }
    ]
    ```
    

# SpringBoot 활용 기능

| 기능 | 사용 목적 |
| --- | --- |
| @RestController, @Service, @RequiredArgsConstructor | Bean 객체로 만들며 DI 종속성 주입 기능, Json 객체로 리스폰스 등 |
| @EventListener | eventBus 형태로 특정 이벤트를 발행하여 비동기식 동작을 하기 위해 사용 |
| @Valid | parameter 검증을 위해 사용 |
| @ExceptionHandler,  @RestControllerAdvice  | exception handling을 위해서 사용 |
| @Slf4j | 로깅을 위해 사용 |
| @Transactional | 트랜잭션 기능 transaction begin → service logic → transaction end |

# 에러 핸들링

<aside>
💡 특정 Exception에 대해 전역 어드바이스 핸들러가 동작합니다. 

1. Bad Request : 400 (잘못된 파라미터 입력시 잘못 입력한 파라미터 내용과 함께 반환합니다.) <br>
2. 외부 API 에러 : 외부 API 호출 에러를 받아서 ErrorResponse 타입으로 에러 내용을 반환합니다. <br>
ex) http://localhost:7712/api/v1/search/blog?query=&size=100&page=100&sort=invalid

</aside>

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        List<String> errors = getErrorsFromFieldErrors(e.getFieldErrors());
        ErrorResponse response =
                createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Bad Request", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(WebExchangeBindException e, ServerWebExchange exchange) {
        List<String> errors = getErrorsFromFieldErrors(e.getFieldErrors());
        ErrorResponse response =
                createErrorResponse(HttpStatus.BAD_REQUEST, exchange.getRequest().getPath().toString(), "Bad Request", errors);
        return ResponseEntity.badRequest().body(response);
    }

		@ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e, HttpServletRequest request) {
        ErrorResponse response =
                createErrorResponse(e.getHttpStatus(), request.getRequestURI(), "외부 API 호출 에러", Collections.singletonList(e.getMessage()));

        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
}
```

```json
{
    "status": 400,
    "timestamp": "2023-03-22T00:36:40.393+00:00",
    "error": "Bad Request",
    "path": "/api/v1/search/blog",
    "errors": [
        "query: 검색어는 필수입니다.",
        "sort: 정렬 방식은 accuracy 또는 recency 중 하나를 선택해야 합니다.",
        "page: 페이지 번호는 50 이하여야 합니다.",
        "size: 페이지 크기는 50 이하여야 합니다."
    ]
}
```

# 테스트 케이스 및 테스트 커버리지

<img width="547" alt="스크린샷 2023-03-21 오후 11 00 46" src="https://user-images.githubusercontent.com/61499337/226783551-13483d4b-a233-4b53-86d8-9909f77fe3b8.png">

1. controller에서 제대로 데이터를 받을 수 있는지 parameter validation은 하고 있는지 등을 테스트 합니다.
2. Kakao Error시 Naver API를 호출하는데 이때 리퀘스트 객체와 리스폰스 객체를 제대로 변환하는지 테스트 합니다.
3. 블로그 검색기능이 제대로 동작하는지 검사합니다. 
    - Kakao API Error를 강제로 만든 후 Navar API 호출하는지 테스트 합니다.
    - Kakao API 요청 성공, 실패, Key = null, 리스폰스가 빈 값인 경우를 테스트합니다.
    - Naver API 요청 성공, 실패에 대해 테스트합니다.
4. 블로그 검색 시 키워드의 검색 카운트를 + 1 증가 시키는 로직을 테스트합니다.
    - 여러 파드에서 돌아가는 애플리케이션이더라도 + 1을 증가시키는데 문제가 없도록 동시 100회 이상의 비동기 요청을 테스트합니다. (단일 애플리케이션이 아니라고 가정하여 syncronize로 구현하지 않았습니다.)
    - 요청 후에 탑텐 리스트 결과가 정상적으로 조회되는지 검증합니다.
    - 키워드 업데이트 및 조회시 에러가 발생하는 상황을 가정합니다.
    
<img width="715" alt="스크린샷 2023-03-21 오후 10 59 33" src="https://user-images.githubusercontent.com/61499337/226783573-e80e66b3-5615-4b51-910f-2b2185685ec3.png">


