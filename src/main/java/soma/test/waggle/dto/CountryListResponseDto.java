package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import soma.test.waggle.entity.Country;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CountryListResponseDto {
    private List<Country> countries;
}
