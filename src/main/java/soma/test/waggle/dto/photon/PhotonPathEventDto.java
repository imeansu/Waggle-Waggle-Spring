package soma.test.waggle.dto.photon;

import lombok.*;
import soma.test.waggle.dto.PhotonMemberInOutDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhotonPathEventDto {

    private Long GameId;
    private Long UserId;
    private String eventName;

    private String sentence;

    private Long sayingMemberId;
    private Long hearingMemberId;
    private Boolean isIn;


    public PhotonConversationDto toConversation() {
        return PhotonConversationDto.builder()
                .memberId(this.UserId)
                .roomId(this.GameId)
                .sentence(this.sentence)
                .build();
    }

    public PhotonMemberInOutDto toMemberInOut(){
        return PhotonMemberInOutDto.builder()
                .sayingMemberId(this.sayingMemberId)
                .hearingMemberId(this.hearingMemberId)
                .isIn(this.isIn)
                .build();
    }
}
