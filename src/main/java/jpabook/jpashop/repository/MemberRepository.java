package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository  // 스프링 빈 등록 (@Repository로 역할을 알기 편함)
@RequiredArgsConstructor
public class MemberRepository {

    // @PersistenceContext  // 스프링이 JPA의 EntityManager를 주입시켜줌 (매우 편리)
    private final EntityManager em;


    public void save(Member member){
        em.persist(member); // 엔티티 메니저에 영속시킴 -> 트랜잭션이 커밋될 때 DB에 insert 쿼리가 실행
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);  // find("객체 타입", pk)
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();   // from의 대상이 테이블이 아닌 엔티티
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // :name 파라티머 바인딩
                .getResultList();
    }
}
