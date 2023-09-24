# Springboot-Jpashop
[인프런/김영한] 

**Springboot, Spring Data Jpa, Querydsl 적용 및 Restful API 개발**

강의 복습 2번 / 개인 PDF 복습 4번

## 프로젝트 소개

JPASHOP 프로젝트를 통해 springboot를 처음 공부하면서 spring의 주요 개념인 solid 개발 방식과 스프링 빈, 컨테이너, 의존성 주입, 제어의 역전 등 기본적인 개념을 익혔습니다.
프로젝트는 크게 회원, 상품, 주문 기능이 있으며 간단한 배송, 주문관리 서비스를 구현하며 학습하였습니다.

중복된 회원이름을 가진 회원은 회원가입이 되지 않으며, 주문 상품을 추가, 수정, 조회하는 기능이 있고, 주문 부분에서는 상품의 개수보다 더 많이 주문할 수 없게 하고
주문을 취소할 경우 상품의 개수를 다시 계산하고 주문 상태 값을 변경합니다. 또한, 회원명과 주문 상태를 통해 주문 내역을 조회하면서 동적쿼리를 JPQL과 Querydsl 2가지
방법으로 구현해봤습니다.


* [도메인 분석 및 설계](#도메인-분석-및-설계)
* [Spring Data Jpa 분석](#spring-data-jpa-적용)
* [Restful API 개발](#restful-api-개발)
* [Querydsl 적용](#querydsl-적용)
* [Stacks](#stacks)
* [화면구성](#화면구성)
* [배운점](#배운점)

---

### 도메인 분석 및 설계

|회원 엔티티 분석|회원 테이블 분석|
|---|---|
|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/c42b2f43-0c88-48a9-aa33-43ff45bbc3c9" width="450px" height="300px" alt="회원 엔티티"></img>|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/388fc1b8-ab72-4794-b088-82382f68124a" width="450px" height="300px" alt="회원 테이블"></img>|

배송 서비스에서 회원 정보, 배송 정보, 주문 상품 정보와 연관이 되어 있는 주문 엔티티를 주로 조회한다. 

주요 로직인 주문 엔티티의 연관관계

일대다 관계(회원-주문)에서는 항상 다쪽(주문)에 외래 키가 있으므로 다쪽을 연관관계의 주인으로 잡습니다.

반대쪽 측에서는 다음과 같이 @OneToMany(mappedBy = "member"), @OneToOne(mappedBy = "delivery", fetch = LAZY)
처럼 mappedBy로 매핑하여 양방향 관계를 설정하여 조회할 수 있도록 합니다.

```java
@Entity
@Table(name="orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)  // @XToOne은 기본 값이 즉시 로딩 -> 지연 로딩으로 변경
    @JoinColumn(name="member_id") // FK 외래키 참조
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // "cascade" : OrderItem 엔티티의 persist를 같이 해줌
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // 일대일 인 경우, 주로 조회하는 테이블에 외래키와 주도권을 가짐
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 나머지 생략...
}
```
[Entity Code](https://github.com/kylo-dev/Springboot-Jpashop/tree/main/src/main/java/jpabook/jpashop/domain)

---

### Spring Data Jpa 적용

순수 JPA를 통해 CRUD를 구현하기 위해선 Repository 빈을 등록하는 클래스를 만들고 이 안에서 CRUD를 구현한 함수들을 작성해줘야 합니다.

예시로 save(), findOne(), findAll(), findByName(), delete() .. 등 하나부터 전부 구현해야 합니다. 

하지만, Spring Data JPA가 제공하는 공통 인터페이스(JpaRepository<T, ID>)를 상속받아 사용하면 주요 CRUD 메소드를 구현하지 않고 사용할 수 있습니다.

공통 인터페이스를 상속한 레포지토리는 컴포넌트 스캔을 Spring Data JPA가 자동으로 처리해주기 때문에 @Repository 애노테이션을 생략할 수 있습니다.

T 부분에는 엔티티 타입을 작성하고 ID에는 엔티티를 식별하는 속성의 타입을 작성합니다.

주요 메서드 (S: 엔티티와 그 자식 타입 | T: 엔티티 | ID: 엔티티의 식별자 타입)
* save(S)
* delete(T)
* findById(ID)
* findAll(...)

순수 JPA -> Spring Data JPA 코드로 수정
```java
public interface OrderJpaRepository extends JpaRepository<Order, Long>, OrderJpaRepositoryCustom {

    @EntityGraph(attributePaths = {"member", "delivery"})
    @Query("select o from Order o")
    List<Order> findAllWithMemberDelivery();

    @EntityGraph(attributePaths = {"member", "delivery", "orderItems", "orderItems.item"})
    @Query("select distinct o from Order o")
    List<Order> findAllWithItem();

    @EntityGraph(attributePaths = {"member", "delivery"})
    @Query("select o from Order o")
    List<Order> findAllWithMemberDelivery(Pageable pageable);
}
```

이 프로젝트에서는 Spring Data JPA로 쿼리 메소드를 다음과 같은 방식으로 생성하였습니다.

1. 메소드 이름으로 쿼리 생성
2. @Query로 레포지토리 메소드에 쿼리 정의

@Query는 실행할 메소드에 정적 쿼리를 직접 작성할 수 있고 애플리케이션 실행 시점에 문법 오류를 발견할 수 있었습니다.

지연 로딩으로 연결된 엔티티들의 정보를 Fetch Join으로 조회하기 위해서 @EntityGraph와 @Query 애노테이션을 사용하였습니다.

[Repository Code](https://github.com/kylo-dev/Springboot-Jpashop/tree/springdatajpa/src/main/java/jpabook/jpashop/repository)

---

### Restful API 개발

[API 명세서 with Postman](https://documenter.getpostman.com/view/28292619/2s9YJW5R13)

Restful API 개발을 하면서 꼭 알아야 할 사항들을 배웠습니다.
1. Request or Response 값은 엔티티로 받는 것이 아닌 별도의 DTO로 받아 처리한다.
    * 엔티티에 프레젠테이션 계층 분리
    * DTO를 통해 다양한 API 요청을 해결하면서 엔티티에 영향을 주지 않는다.
2. 모든 엔티티의 관계는 즉시 로딩이 아닌 지연로딩으로 설정한다.
   * 지연 로딩 조회시 최적화를 위해 컬렉션이 아닌 경우는 Fetch Join을 통해 거의 해결이 된다.
   * 페이징이 필요한 경우 컬렉션은 Fetch Join 하지 않고 batch_fetch_size or @BatchSize를 통해 최적화한다.
3. 엔티티를 DTO로 변환해 조회하는 방식으로 해결이 되지 않는 경우 -> DTO 직접 조회 방식으로 최적화한다.

---

### Querydsl 적용

|사용자 정의 레포지토리|
|---|
|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/80e99eb8-e32b-4390-bc01-ce194e82e179" width="480px" height="350px" alt="회원 엔티티"></img>|

JPQL로 동적 쿼리를 구현할 수 있지만 코드의 가독성과 유지보수 측면에선 좋지 않아 이 단점을 보완한 Querydsl을 통해서도 동적 쿼리를 구현해보았습니다.

BooleanBuilder와 Where 절에 다중 파라미터를 사용하는 방법 중 저는 Where 절에 다중 파라미터를 적용하여 구현했습니다.

Where 조건에 null 일 올 경우 무시한 상태로 join과 select를 실행합니다. 

또한 where에 적용할 조건들을 메서드로 따로 만들어 다른 쿼리에서도 재활용할 수 있으며, JPQL의 코드보다 가독성이 높아집니다.
```java
@RequiredArgsConstructor
@Repository
public class OrderJpaRepositoryImpl implements OrderJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> search(OrderSearch orderSearch) {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member)
                .where(orderStatusEq(orderSearch), orderMemberNameEq(orderSearch))
                .fetch();
    }
    private BooleanExpression orderStatusEq(OrderSearch orderSearchCond) {
        return orderSearchCond.getOrderStatus() != null ? order.status.eq(orderSearchCond.getOrderStatus()) : null;
    }
    private BooleanExpression orderMemberNameEq(OrderSearch orderSearchCond) {
        return orderSearchCond.getMemberName() != null ? order.member.name.contains(orderSearchCond.getMemberName()) : null;
    }
}
```

[JPQL 동적 쿼리 보기 - findAllByString](https://github.com/kylo-dev/Springboot-Jpashop/blob/main/src/main/java/jpabook/jpashop/repository/OrderRepository.java)

---

### Stacks
#### Environment
<div>
    <img src="https://img.shields.io/badge/Intellij-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
    <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">
    <img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=github&logoColor=white">
    <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
</div>

#### Development
<div>
    <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
    <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
    <img src="https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white">
</div>

---

### 화면구성

|메인 페이지|회원등록 페이지|상품등록 페이지|
|---|---|---|
|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/dcb82fb1-a7b2-4579-9492-95cb9404368b" width="450px" height="400px" alt="메인 페이지"></img>|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/40b43759-0a57-4ea4-b3a6-fe540e60f0d6" width="450px" height="400px" alt="회원등록 페이지"></img>|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/f4282110-7e24-4be2-968d-79648a73c53f" width="450px" height="400px" alt="상품등록 페이지"></img>|
|상품주문 페이지|주문조회 페이지|상품조회 페이지|
|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/fb33251b-c827-4690-99ba-d59606231249" width="400px" height="330px" alt="상품주문 페이지"></img>|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/5ef13814-b4c3-48c5-b481-19f221493b72" width="400px" height="330px" alt="주문조회 페이지"></img>|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/90bbe1dc-82c4-4acf-a34a-5ec1bfbe2ad2" width="450px" height="400px" alt="상품조회 페이지"></img>|

---

### 배운점

> MVC 방식으로 개발해보면서 각 기능을 패키지화, 분리해보고 Model, View, Controller 사이에 어떤 흐름으로 데이터와 로직이 전달되는지 학습했습니다.

>Thymleaf 문법을 이용해 Website를 만들어보며 Controller <-> View 에서 데이터를 서로 주고 받는 방법, Model <-> Controller 에서 Service 클래스를 통해 CRUD 로직을 전달해
>비즈니스 로직을 구현하였습니다.

>Member, Order, Delivery, Order_Item, Item, Category의 여러 엔티티를 설계하고 연관관계를 매핑하면서 일대다, 다대일, 양방향, 단방향 관계에 대해 학습하고 
>JPA를 통해 테이블을 생성할 때에 연관 관계에서 어느 부분을 주인으로 설정하면 되는지 코드로 구현해보며 결과를 확인하였습니다.

>Lombok과 Spring Data Jpa를 적용해보면서 주요 어노테이션을 익히고 JPQL query 방식과 JPA query 작성법을 학습하며 백엔드 개발의 핵심인 CRUD 기능을 익혔습니다.

>Restful api 개발을 하면서 (1) 엔티티가 아닌 별도의 DTO 클래스를 통해 데이터를 요청받고 응답하는 방법과 이유를 학습했으며, (2) 데이터베이스 조회 성능을 최적화 하기 위해 
>지연 로딩(Lazy)은 어떻게 처리하면 되는지? XToMany 컬렉션 관계는 어떻게 조회하면 최적화할 수 있는지 학습하며 Postman을 통해 API를 개발해봤습니다.

>마지막으로 jpashop에서 JPQL로 작성된 부분을 1차로 JPA 방법으로 바꿔보고 2차로 Querydsl을 적용해보며 보다 효율적으로 동적쿼리를 짜는 방법에 대해 배웠습니다.

