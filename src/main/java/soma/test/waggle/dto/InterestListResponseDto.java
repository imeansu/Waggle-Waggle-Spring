package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import soma.test.waggle.entity.Interest;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class InterestListResponseDto {
    private List<Interest> interests;
}
