package soma.test.waggle.entity;

import lombok.*;
import soma.test.waggle.type.OnStatusType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Enumerated(value = EnumType.STRING)
    private OnStatusType isLast;

    private LocalDateTime joinTime;

    private LocalDateTime leaveTime;
}
