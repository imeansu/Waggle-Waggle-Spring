package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soma.test.waggle.redis.repository.RedisSentenceDto;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor
public class Sentence implements Serializable {

    @Id @GeneratedValue
    @Column(name = "sentance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String sentence;

    private LocalDateTime dateTime;

    @Builder
    public Sentence(Conversation conversation, Member member, String sentence, LocalDateTime dateTime) {
        this.conversation = conversation;
        this.member = member;
        this.sentence = sentence;
        this.dateTime = dateTime;
    }

    public static RedisSentenceDto toRedisDto(Sentence sentence) {
        return RedisSentenceDto.builder()
                .conversationId(sentence.getConversation().getVivoxId())
                .memberId(sentence.getMember().getId())
                .sentence(sentence.getSentence())
                .dateTime(sentence.getDateTime())
                .build();
    }

}
