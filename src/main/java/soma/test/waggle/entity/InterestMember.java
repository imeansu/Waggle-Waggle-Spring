package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Interest - member 연결 중간 테이블
 * ManyToMany 를 피하기 위해
 * */
@Entity @Getter
@Builder
@NoArgsConstructor
public class InterestMember {

    @Id @GeneratedValue
    @Column(name = "interest_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 관심사를 enum으로 관리?
    // 추가할 경우 어려움... 동적으로 관리하기 편하게
    // 새로 interest entity 만들고 동적으로 관리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;
}
