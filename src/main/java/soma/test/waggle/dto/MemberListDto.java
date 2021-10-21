package soma.test.waggle.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberListDto {
    private int size;
    private List<MemberInfoDto> members;
}
