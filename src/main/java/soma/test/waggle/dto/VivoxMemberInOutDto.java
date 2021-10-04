package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VivoxMemberInOutDto {

    private Long hearingMemberId;
    private Long sayingMemberId;
    private Long roomId;

}
