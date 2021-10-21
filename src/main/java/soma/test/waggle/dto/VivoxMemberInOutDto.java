package soma.test.waggle.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VivoxMemberInOutDto {

    private Long hearingMemberId;
    private Long sayingMemberId;
    private Long roomId;

}
