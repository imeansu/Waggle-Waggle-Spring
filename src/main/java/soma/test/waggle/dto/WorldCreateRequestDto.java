package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldCreateRequestDto {

    private String name;
    private String topic;
    private WorldMap map;
    private int people;
    private LocalDateTime dateTime;
    private String photon_server;
    private OnStatus onStatus;
    private List<String> keywords;
    private World world;

    public static WorldCreateRequestDto of(WorldRoom worldRoom) {
        return WorldCreateRequestDto.builder()
                .name(worldRoom.getName())
                .topic(worldRoom.getTopic())
                .map(worldRoom.getMap())
                .people(worldRoom.getPeople())
                .dateTime(worldRoom.getDateTime())
                .photon_server(worldRoom.getPhoton_server())
                .onStatus(worldRoom.getOnStatus())
                .keywords(worldRoom.getKeywords())
                .world(worldRoom.getWorld())
                .build();
    }

    public WorldRoom toWorldRoom() {
        return WorldRoom.builder()
                .name(name)
                .topic(topic)
                .map(map)
                .people(people)
                .dateTime(LocalDateTime.now())
                .photon_server(photon_server)
                .onStatus(onStatus)
                .keywords(WorldRoom.keywordListToString(keywords))
                .world(world)
                .build();
    }
}
