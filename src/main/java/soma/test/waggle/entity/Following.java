package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder @Getter
public class Following {

    @Id @GeneratedValue
    @Column(name = "following_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member followedMember;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member followingMember;

    private LocalDateTime dateTime;
}
