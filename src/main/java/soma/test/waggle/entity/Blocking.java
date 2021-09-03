package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter
@Builder
public class Blocking {

    @Id @GeneratedValue
    @Column(name = "blocking_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member blockedMember;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member blockingMember;

    private LocalDateTime dateTime;
}
