package soma.test.waggle.dto.photon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotonConversationDto {

    private Long roomId;
    private Long memberId;
    private String sentence;

    // vivoxId 를 roomId 로 할 수 있는지 확인 중...
    //    private String vivoxId;

}
