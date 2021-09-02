package soma.test.waggle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class WorldRoom {


    @Id @GeneratedValue
    @Column(name = "world_room_id")
    private Long id;

//    @NotNull
    private String name;
    private String topic;

    @Enumerated(value = EnumType.STRING)
    private WorldMap map;

//    @NotNull
    private int people;
//    @NotNull
    private LocalDateTime dateTime;
//    @NotNull
    private String photon_server;

    private OnStatus onStatus;

    @ManyToOne(fetch = FetchType.LAZY)
//    @NotNull
    @JoinColumn(name = "world_id")
    private World world;

    @OneToMany(mappedBy = "worldRoom")
    List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "worldRoom")
    List<EntranceRoom> entranceRooms = new ArrayList<>();

}
