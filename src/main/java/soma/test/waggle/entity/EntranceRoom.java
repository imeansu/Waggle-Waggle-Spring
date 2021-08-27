package soma.test.waggle.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class EntranceRoom {

    @Id @GeneratedValue
    @Column(name = "entrance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "world_room_id")
    private WorldRoom worldRoom;

    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private String isLast;
}
