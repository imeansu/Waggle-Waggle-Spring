package soma.test.waggle.dto.photon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import soma.test.waggle.dto.PhotonMemberInOutDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotonPathEventDto {

    @JsonProperty("GameId")
    private Long GameId;
    @JsonProperty("UserId")
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
