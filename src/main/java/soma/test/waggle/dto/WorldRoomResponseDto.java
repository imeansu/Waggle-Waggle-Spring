package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.type.WorldMapType;
import soma.test.waggle.entity.WorldRoom;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldRoomResponseDto {

    private Long id;
    private String name;
    private String topic;
    private WorldMapType map;
    private int people;
    private LocalDateTime dateTime;
    private List<String> keywords;
    private String photon_server;

    public static WorldRoomResponseDto of(WorldRoom worldRoom){
        return new WorldRoomResponseDto().builder()
                .id(worldRoom.getId())
                .name(worldRoom.getName())
                .topic(worldRoom.getTopic())
                .map(worldRoom.getMap())
                .people(worldRoom.getPeople())
                .dateTime(worldRoom.getDateTime())
                .photon_server(worldRoom.getPhoton_server())
                .keywords(worldRoom.getKeywords())
                .build();
    }


}
