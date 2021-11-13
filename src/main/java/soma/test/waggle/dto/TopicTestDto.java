package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicTestDto {

    private List<String> members;
    private List<String> sentences;

}
