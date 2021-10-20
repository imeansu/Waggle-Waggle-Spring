package soma.test.waggle.entity;

import lombok.*;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.type.WorldMapType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity @Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class
WorldRoom {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "world_room_id")
    private Long id;

//    @NotNull
    private String name;
    private String topic;

    @Enumerated(value = EnumType.STRING)
    private WorldMapType map;

//    @NotNull
    private int people;
//    @NotNull
    private LocalDateTime dateTime;
//    @NotNull
    private String photon_server;

    private OnStatusType onStatusType;

    @ManyToOne(fetch = FetchType.LAZY)
//    @NotNull
    @JoinColumn(name = "world_id")
    private World world;

    @OneToMany(mappedBy = "worldRoom")
    List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "worldRoom")
    List<EntranceRoom> entranceRooms = new ArrayList<>();

    private String keywords;

    public List<String> getKeywords() {
        if (this.keywords != null && keywords.length() > 0) {
            return Arrays.asList(this.keywords.split(","));
        }
        return new ArrayList<>();
    }

    public void setKeywords(List<String> keywords){
        if (keywords != null && keywords.size() > 0) {
            this.keywords = keywords.stream().map(k -> String.valueOf(k)).collect(Collectors.joining(","));
        }
        else{
            this.keywords = new String();
        }
    }

    public static String keywordListToString(List<String> list){
        if (list != null && list.size() > 0) {
            return list.stream().map(k -> String.valueOf(k)).collect(Collectors.joining(","));
        }
        else{
            return new String();
        }
    }
}
