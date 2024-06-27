### MySQL 로컬 개발
[맥OS 로컬에서 MySQL 설치 및 실행](https://velog.io/@inyong_pang/MySQL-%EC%84%A4%EC%B9%98%EC%99%80-%EC%B4%88%EA%B8%B0-%EC%84%A4%EC%A0%95)
- 스프링에서의 설정
  1. build.gradle에 `runtimeOnly 'com.mysql:mysql-connector-j'` 추가
  2. application.yml의 spring.jpa.properties.hibernate에 `dialect: org.hibernate.dialect.MySQLDialect`을 추가한다.
  3. application.yml에 아래 코드와 같이 MySQL에 대한 연결 설정 정보 추가
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/(스키마 이름)
    username: root
    password: (설정한 비밀번호)
    driver-class-name: com.mysql.cj.jdbc.Driver
```



### JPA 기본 키 생성 전략
- **IDENTITY**
  - `@GeneratedValue(strategy=GenerationType.INDENTITY)`
  - 테이블 내에 기본 키를 담당하는 id 컬럼을 생성 
  - id가 NULL인 경우 자동으로 값이 증가
  - 데이터를 생성할 때, 쿼리를 날리기 전에는 id를 설정해두지 않고 INSERT 쿼리를 날리면 id를 생성
    - `em.persist()` 메소드 실행 시 즉시 INSERT 쿼리를 날려 DB에 저장한다.
  - **단점**
    - 아직 DB에 저장되지 않은 객체라면 영속성 컨텍스트에서 관리할 수 없다
      - 영속성 컨텍스트에서 객체를 관리하려면 기본 키가 필수인데, DB에 INSERT 된 후에야 id를 할당하기 때문
      - JPA 입장에서는 영속성 컨텍스트 Map에 넣을 Key가 없기 때문에 넣을 수 없다
- **SEQUENCE**
  - `@GeneratedValue(strategy=GenerationType.SEQUENCE)`
  - Unique한 값을 순서대로 생성하고 이를 객체의 기본 키로써 할당하는 객체이며 JPA가 아닌 DB에서 관리한다
  - 구조
    1. DB에서 특정 엔티티의 다음 기본 키 값을 시퀀스 테이블에 저장해둔다.
    2. 애플리케이션에서 해당 엔티티를 사용하면 시퀀스 테이블에 있는 값부터 미리 설정된 크기(allocationSize)만큼의 기본 키값을 미리 캐시해온다
    3. `em.persist()`시 바로 INSERT하지 않고 시퀀스의 id값만 넣어줘서 영속성 컨텍스트에 영속한다
    4. 트랜잭션 커밋 시 한번에 쿼리를 날린다
> 스프링에서 MySQL의 기본 전략값은 IDENTITY인데 기본값으로 DB 초기화 시 이상하게 '@@@_seq'테이블이 생성된다. 
> `strategy=GenerationType.IDENTITY`로 설정하면 '@@@_seq'테이블이 생성되지 않는다. 왜 이런지는 모르겠다.



### 즉시 로딩(EAGER) VS 지연 로딩(LAZY)
- **즉시 로딩(EAGER)**
  - @ToOne 관계는 fetch의 기본값으로 즉시 로딩이 설정되어 있다.
  - 즉시 로딩은 특정 엔티티를 불러올 때, 해당 엔티티와 매핑된 다른 엔티티를 불러오는 쿼리를 함께 보내는 것이다.
  - 즉시 로딩은 가급적 피하는 것이 좋다.
    - 예를 들어, Team과 Member 엔티티가 1:1000으로 매핑되어 있을 때 Team 객체를 호출하면 Member 엔티티를 호출하는 쿼리 1000개가 추가로 발생한다.
- **지연 로딩(LAZY)**
  - @ToMany 관계는 fetch의 기본값으로 지연 로딩이 설정되어 있다.
  - 지연 로딩은 특정 엔티티를 불러올 때, 해당 엔티티 하나만 호출한다.
  - 매핑된 다른 엔티티가 필요해지면 그 때 쿼리를 생성해 매핑된 엔티티를 호출하는 방식이다.
  - 즉, 쿼리를 최소화시킬 수 있으므로 **지연 로딩을 사용하는 것이 좋다.**