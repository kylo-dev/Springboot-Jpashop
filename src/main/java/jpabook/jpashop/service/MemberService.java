package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기 전용 - 최적화 해줌 (JPA의 모든 데이터 변경은 무조건 트랜잭션 안에서 실행되어야 함)
@RequiredArgsConstructor   // 스프링 부트 (Lombok) 자동으로 final 필드를 생성자 코드를 만들어 주입
public class MemberService {

    // private final MemberRepository memberRepository;
    private final MemberJpaRepository memberJpaRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberJpaRepository.save(member);
        return member.getId();
    }

    // 회원 중복 확인
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberJpaRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberJpaRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberJpaRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    }


    @Transactional
    public void update(Long id, String name) {
        Member member = memberJpaRepository.findById(id)
                        .orElseThrow(IllegalArgumentException::new);
        member.setName(name); // 변경 감지로 Update
    }
}
