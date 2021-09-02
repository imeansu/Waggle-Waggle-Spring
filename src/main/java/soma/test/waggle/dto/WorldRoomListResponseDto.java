package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.WorldRoom;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldRoomListResponseDto {

    private int size;
    private List<WorldRoomResponseDto> worldRoomList;

}
