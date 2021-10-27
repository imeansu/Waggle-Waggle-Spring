package soma.test.waggle.redis.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicRequestMessage implements Serializable {
//    private String conversationId;
    private List<String> sentences;
    private List<String> members;
}
