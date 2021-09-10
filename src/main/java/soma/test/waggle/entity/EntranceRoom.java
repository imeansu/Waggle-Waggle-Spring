package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
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
    private OnStatus isLast;

    private LocalDateTime joinTime;

    private LocalDateTime leaveTime;
}
