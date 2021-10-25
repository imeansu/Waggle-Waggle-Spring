package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.type.WorldMapType;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldCreateRequestDto {

    private Long room_id;
    private String name;
//    private String topic;
    private WorldMapType map;
    private int people;
    private LocalDateTime dateTime;
//    private String photon_server;
    private OnStatusType onStatus;
    private List<String> keywords;
//    private World world;

    public static WorldCreateRequestDto of(WorldRoom worldRoom) {
        return WorldCreateRequestDto.builder()
                .room_id(worldRoom.getId())
                .name(worldRoom.getName())
//                .topic(worldRoom.getTopic())
                .map(worldRoom.getMap())
                .people(worldRoom.getPeople())
                .dateTime(LocalDateTime.now())
//                .photon_server(worldRoom.getPhoton_server())
                .onStatus(worldRoom.getOnStatus())
                .keywords(worldRoom.getKeywords())
                .build();
    }

    public WorldRoom toWorldRoom() {
        return WorldRoom.builder()
                .name(name)
//                .topic(topic)
                .map(map)
                .people(people)
                .dateTime(LocalDateTime.now())
//                .photon_server(photon_server)
                .onStatus(onStatus)
                .keywords(WorldRoom.keywordListToString(keywords))
                .build();
    }
}
