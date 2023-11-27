package jpabook.jpashop.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.Hello;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;

import static jpabook.jpashop.QHello.hello;


@SpringBootTest
@Transactional
public class QuerydslApplicationTests {

    @Autowired
    EntityManager em;

    JPAQueryFactory query;

    @BeforeEach
    public void before() {
        query = new JPAQueryFactory(em);
    }

    @Test
    void contextLoads() {
//        Hello hello = new Hello();
//        em.persist(hello);

        Hello result = query
                .selectFrom(hello)
                .fetchOne();

//        Assertions.assertThat(result).isEqualTo(hello);
//
//        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
    }
}
