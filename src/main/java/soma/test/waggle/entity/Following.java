package soma.test.waggle.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class Following {

    @Id @GeneratedValue
    @Column(name = "following_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member followedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member followingMember;

    private LocalDateTime dateTime;
}
