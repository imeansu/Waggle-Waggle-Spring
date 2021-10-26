package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.dto.photon.PhotonConversationDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhotonMemberInOutDto {

    private Long roomId;
    private Long sayingMemberId;
    private Long hearingMemberId;
    private Boolean isIn;

}
