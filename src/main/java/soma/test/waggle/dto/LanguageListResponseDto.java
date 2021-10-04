package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import soma.test.waggle.entity.Language;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class LanguageListResponseDto {
    private List<Language> languages;
}
