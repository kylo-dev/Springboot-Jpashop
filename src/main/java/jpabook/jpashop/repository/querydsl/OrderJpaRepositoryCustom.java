package jpabook.jpashop.repository.querydsl;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;

import java.util.List;

public interface OrderJpaRepositoryCustom {

    List<Order> search(OrderSearch orderSearch);
}
