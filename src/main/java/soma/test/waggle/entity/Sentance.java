package soma.test.waggle.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Sentance {

    @Id @GeneratedValue
    @Column(name = "sentance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String sentence;

    private LocalDateTime dateTime;
}
