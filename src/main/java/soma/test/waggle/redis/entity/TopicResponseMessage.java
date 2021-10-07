package soma.test.waggle.redis.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicResponseMessage implements Serializable {
    private String conversationId;
    private List<String> topics;
    private List<String> members;
}
