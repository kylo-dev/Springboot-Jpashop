# Springboot-Jpashop
[인프런/김영한] Springboot, Spring Data Jpa, Querydsl 적용 및 Restful API 개발

강의 복습 2번 / 개인 PDF 복습 4번

## 프로젝트 소개 및 배운점

JPASHOP 프로젝트는 크게 회원, 상품, 주문 기능이 있으며 간단한 배송, 주문관리 서비스를 구현하였습니다.

이 프로젝트로 springboot를 처음 공부하면서 spring의 주요 개념인 solid 개발 방식과 스프링 빈, 컨테이너, 의존성 주입, 제어의 역전 등 기본적인 개념을 학습했습니다.

또한, MVC 방식으로 개발해보면서 각 기능을 패키지화, 분리해보고 Model, View, Controller 사이에 어떤 흐름으로 데이터와 로직이 전달되는지 학습했습니다. 

Thymleaf 문법을 이용해 Website를 만들어보며 Controller <-> View 에서 데이터를 서로 주고 받는 방법, Model <-> Controller 에서 Service 클래스를 통해 CRUD 로직을 전달해
비즈니스 로직을 구현하였습니다.

Member, Order, Delivery, Order_Item, Item, Category의 여러 엔티티를 설계하고 연관관계를 매핑하면서 일대다, 다대일, 양방향, 단방향 관계에 대해 학습하고 
JPA를 통해 테이블을 생성할 때에 연관 관계에서 어느 부분을 주인으로 설정하면 되는지 코드로 구현해보며 결과를 확인하였습니다.

Lombok과 Spring Data Jpa를 적용해보면서 주요 어노테이션을 익히고 JPQL query 방식과 JPA query 작성법을 학습하며 백엔드 개발의 핵심인 CRUD 기능을 익혔습니다.

Restful api 개발을 하면서 (1) 엔티티가 아닌 별도의 DTO 클래스를 통해 데이터를 요청받고 응답하는 방법과 이유를 학습했으며, (2) 데이터베이스 조회 성능을 최적화 하기 위해 
지연 로딩(Lazy)은 어떻게 처리하면 되는지? XToMany 컬렉션 관계는 어떻게 조회하면 최적화할 수 있는지 학습하며 Postman을 통해 API를 개발해봤습니다.

마지막으로 jpashop에서 JPQL로 작성된 부분을 1차로 JPA 방법으로 바꿔보고 2차로 Querydsl을 적용해보며 보다 효율적으로 동적쿼리를 짜는 방법에 대해 배웠습니다.


* [도메인 분석 및 설계](#도메인-분석-및-설계)
* [Spring Data Jpa 분석](#spring-data-jpa-분석)
* [Restful API 개발](#restful-api-개발)
* [Querydsl 적용](#querydsl-적용)



### 도메인 분석 및 설계


### Spring Data Jpa 분석

### Restful API 개발

### Querydsl 적용

### Stacks

### 화면구성
