package soma.test.waggle.redis.repository;

import lombok.*;
import soma.test.waggle.entity.Conversation;
import soma.test.waggle.entity.Member;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
//@AllArgsConstructor
public class RedisSentenceDto implements Serializable {

    private String conversationId; // vivoxId
    private Long memberId;
    private String sentence;
    private LocalDateTime dateTime;

    @Builder
    public RedisSentenceDto(String conversationId, Long memberId, String sentence, LocalDateTime dateTime) {
        this.conversationId = conversationId;
        this.memberId = memberId;
        this.sentence = sentence;
        this.dateTime = dateTime;
    }
}
