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
    private World world;

    

}
