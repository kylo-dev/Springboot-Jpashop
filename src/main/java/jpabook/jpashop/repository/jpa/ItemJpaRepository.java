package jpabook.jpashop.repository.jpa;

import jpabook.jpashop.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
