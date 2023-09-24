# Springboot-Jpashop
[인프런/김영한] Springboot, Spring Data Jpa, Querydsl 적용 및 Restful API 개발

강의 복습 2번 / 개인 PDF 복습 4번

## 프로젝트 소개

JPASHOP 프로젝트를 통해 springboot를 처음 공부하면서 spring의 주요 개념인 solid 개발 방식과 스프링 빈, 컨테이너, 의존성 주입, 제어의 역전 등 기본적인 개념을 익혔습니다.
프로젝트는 크게 회원, 상품, 주문 기능이 있으며 간단한 배송, 주문관리 서비스를 구현하며 학습하였습니다.

중복된 회원이름을 가진 회원은 회원가입이 되지 않으며, 주문 상품을 추가, 수정, 조회하는 기능이 있고, 주문 부분에서는 상품의 개수보다 더 많이 주문할 수 없게 하고
주문을 취소할 경우 상품의 개수를 다시 계산하고 주문 상태 값을 변경합니다. 또한, 회원명과 주문 상태를 통해 주문 내역을 조회하면서 동적쿼리를 JPQL과 Querydsl 2가지
방법으로 구현해봤습니다.


* [도메인 분석 및 설계](#도메인-분석-및-설계)
* [Spring Data Jpa 분석](#spring-data-jpa-분석)
* [Restful API 개발](#restful-api-개발)
* [Querydsl 적용](#querydsl-적용)
* [배운점](#배운점)



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


### Spring Data Jpa 분석

### Restful API 개발

### Querydsl 적용

// draw.io로 수정하기
|사용자 정의 레포지토리|
|---|
|<img src="https://github.com/kylo-dev/Springboot-Jpashop/assets/103489352/f9fd0f5d-7019-461d-92b2-f758f5593751" width="450px" height="300px" alt="회원 엔티티"></img>|

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



### Stacks

### 화면구성

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

