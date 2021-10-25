package soma.test.waggle.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Interest - member 연결 중간 테이블
 * ManyToMany 를 피하기 위해
 * */
@Entity @Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestMember {

    @Id @GeneratedValue
    @Column(name = "interest_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;
}
