package jpabook.jpashop.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

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
