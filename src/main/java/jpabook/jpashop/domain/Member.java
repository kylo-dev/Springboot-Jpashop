package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id") // 컬럼명
    private Long id;

    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member") // 매핑된 거울, 읽기 전용
    // 연관관계의 주인이 아닌 것을 표시하는 설정, Order의 memeber에 매핑되어 있음
    private List<Order> orders = new ArrayList<>();
}
