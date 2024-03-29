package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import soma.test.waggle.type.LanguageType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class LanguageListResponseDto {
    private List<LanguageType> languages;
}
