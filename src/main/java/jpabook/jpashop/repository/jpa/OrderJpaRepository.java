package jpabook.jpashop.repository.jpa;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.querydsl.OrderJpaRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
