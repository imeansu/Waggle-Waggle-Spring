package soma.test.waggle.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conversation {

    @Id @GeneratedValue
    @Column(name = "conversation_id")
    private Long id;

    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_room_id")
    private WorldRoom worldRoom;

    @OneToMany(mappedBy = "conversation")
    List<Sentance> sentances = new ArrayList<>();
}
