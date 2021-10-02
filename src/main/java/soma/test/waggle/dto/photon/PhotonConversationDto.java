package soma.test.waggle.dto.photon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class PhotonConversationDto {

    private Long roomId;
    private String vivoxId;
    private Long memberId;
    private String sentence;

}
