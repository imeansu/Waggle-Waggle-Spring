package soma.test.waggle.entity;

import javax.persistence.*;

@Entity
public class Interest {

    @Id @GeneratedValue
    @Column(name = "interest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String interestSubject;
}
