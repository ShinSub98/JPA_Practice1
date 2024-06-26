### 스프링에서 외부 API 호출 방법
다양한 방법이 존재하지만 현재는 최신 인터페이스인 **WebClient**가 가장 많이 사용된다.
1. 스프링 5.0에 추가된 인터페이스(현재 6.18) (스프링 버전은 의존성에서 `spring-core`를 검색하면 확인할 수 있다.)
2. 비동기화 방식
   - WebClient의 전신인 RestTemplate은 Http 요청이 동기적으로 이루어져 요청을 보내면 응답이 올 때까지 다른 작업을 할 수 없었다.
3. IO의 작업 방식(Blocking / Non-Blocking)을 자유롭게 변경 가능하다
   - Non-Blocking으로 설정하면 IO(입출력)작업 중, 작업이 완료되지 않아도 프로그램이나 스레드가 다른 작업을 수행할 수 있는 방식이다.
   - 따라서 Non-Blocking과 비동기적 방식이 결합되면 다중 IO 작업의 효율이 증가한다.

**사용 방법**
1. 의존성 추가: `implementation 'org.springframework.boot:spring-boot-starter-webflux'`
2. Builder를 사용해 WebClient를 생성
3. 외부 API 호출
```java
static WebClient webClient = WebClient.create("https://example.com"); // 외부 API의 baseUrl. 지정하지 않아도 된다.

public ExternalApiDto useExternalApi(int postId) {
   return webClient.get() // 외에도 .post(), .put(), .patch(), .delete()... 많다
           .url("/posts/{id}", postId) // 엔드포인트 
           .retrieve() // 응답의 body만 받아 제공하는 메소드. status와 헤더 모두 필요하다면 exchange() 메소드를 사용
           .bodyToMono(ExternalApiDto.class) // 여기서 ExternalApiDto는 외부 API와 통신하기 위한 DTO이다.
           .block(); // 기본적으로 Blocking 방식으로 작동한다.
}
```
> webflux는 반응형 프로그래밍(Reactive Programming)을 구현하기 위해 Reactor라는 라이브러리를 이용한다.
> 반응형 프로그래밍이란 '이벤트 기반의 비동기식 애플리케이션'으로, 기존 반복문 기반의 프로그래밍과 달리 이벤트 스트림을 이용한다.<br>
> 쉽게 말하면 기존에는 반복문 돌리는 동안 아무 것도 못했는데 이제는 반복문 돌리면서 딴짓도 가능하다는 의미이다.<br><br>
> 이 때 필요한 것이 `Mono`와 `Flux`이다. Mono와 Flux는 데이터 개수만 차이가 있는데 Mono는 0~1, Flux는 복수의 데이터를 받을 수 있다. Mono와 Flux는 모든 타입을 받을 수 있는 인터페이스로 위의 예시에서도 우리가 직접 만든 DTO 클래스로 변환하여 사용하였다.