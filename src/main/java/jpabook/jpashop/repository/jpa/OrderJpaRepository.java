package jpabook.jpashop.repository.jpa;

import jpabook.jpashop.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

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
