### 환경변수 설정 방법
- **IntelliJ**에 환경변수를 저장하는 방법
  - 우측 상단 버튼 왼쪽 셀렉트 박스를 눌러 `Edit Configuration`로 이동
  - `Modify options` → `Envirionment variables`의 우측 문서 버튼 클릭
  - 새로운 환경변수 등록
- **yml 파일**에 환변수를 저장하는 방법(이 방법은 별도의 설정을 필요로 하지 않는다.)
  - `application.yml`과 같은 경로에 `application-@@@.yml`이라는 이름의 파일 생성
  - `application.yml`처럼 똑같이 작성한다.
  - 아래와 같이 작성하면 `application.yml`에 해당 파일의 내용이 추가된다.
```yml
spring:
  ...
  profiles.include:
    - @@@
```
- **application.yml**에서 환경변수를 불러오는 방법
  - IDE 혹은 OS에 저장된 경우: `${환경변수명}`
- **프로젝트**에서 환경변수를 불러오는 방법
  - `application.yml`의 `spring.datasource.password`를 불러오려면 아래와 같이 필드를 생성한다.
```java
@Value("${spring.datasource.password}")
    private String password;
```