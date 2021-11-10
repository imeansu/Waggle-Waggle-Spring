package soma.test.waggle.dto.photon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotonRoomIdDto {
    @JsonProperty("GameId")
    private Long GameId;
}
