package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor
@Table(indexes = @Index(name = "IDX_VIVOX", unique = true, columnList = "vivox_id"))
public class Conversation implements Serializable {

    @Id @GeneratedValue
    @Column(name = "conversation_id")
    private Long id;

    @Column(name = "vivox_id")
    private String vivoxId;

    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_room_id")
    private WorldRoom worldRoom;

    @OneToMany(mappedBy = "conversation")
    List<Sentence> sentences = new ArrayList<>();

    @Builder
    public Conversation(String vivoxId, WorldRoom worldRoom, LocalDateTime dateTime) {
        this.vivoxId = vivoxId;
        this.worldRoom = worldRoom;
        this.dateTime = dateTime;
    }

}
