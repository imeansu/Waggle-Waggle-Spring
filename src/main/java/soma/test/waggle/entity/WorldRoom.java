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

    // 배치 공간 문제로 보류
//    private String topic;

    @Enumerated(value = EnumType.STRING)
    private WorldMapType map;

//    @NotNull
    private int people;
//    @NotNull
    private LocalDateTime dateTime;

    // worldRoom id 로 서버 이름 지정 가능할 것 같다고 하여 보류
//    @NotNull
//    private String photon_server;

    @Enumerated(value = EnumType.STRING)
    private OnStatusType onStatus;

    @ManyToOne(fetch = FetchType.LAZY)
//    @NotNull
    @JoinColumn(name = "world_id")
    private World world;

    @OneToMany(mappedBy = "worldRoom")
    List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "worldRoom")
    List<EntranceRoom> entranceRooms = new ArrayList<>();

    private String keywords;

    // 문자열로 저장된 키워드를 List로 변환
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
