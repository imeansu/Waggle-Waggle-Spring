package soma.test.waggle.dto.photon;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhotonConversationDto {

    private Long roomId;
    private String vivoxId;
    private Long memberId;
    private String sentence;

}
