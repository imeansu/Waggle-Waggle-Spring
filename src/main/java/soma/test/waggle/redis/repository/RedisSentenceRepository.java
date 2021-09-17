package soma.test.waggle.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Sentence;
import soma.test.waggle.repository.ConversationRepositoty;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.repository.SentenceRepository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisSentenceRepository {

    private final SentenceRepository sentenceRepository;
    private final MemberRepository memberRepository;
    private final ConversationRepositoty conversationRepositoty;

    private final RedisTemplate redisTemplate;
    @Resource(name = "redisTemplate")
    private ListOperations<String, RedisSentenceDto> listOperations;

    private static final String KEY_CONVERSATION_SENTENCE = "conversation:sentence:";

    public boolean addSentenceToRedis(RedisSentenceDto redisSentenceDto, String conversationId){
        if (listOperations.rightPush(KEY_CONVERSATION_SENTENCE+conversationId, redisSentenceDto) > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public List<RedisSentenceDto> getSentenceFromRedis(String conversationId){
        return listOperations.range(KEY_CONVERSATION_SENTENCE+conversationId, 0, -1);
    }

    public boolean migrationSentencetoDB(String conversationId){
        List<RedisSentenceDto> sentenceDtos = getSentenceFromRedis(conversationId);
        List<Sentence> sentences = sentenceDtos.stream()
                .map(dto -> toSentence(dto))
                .collect(Collectors.toList());
        sentenceRepository.saveAll(sentences);
        removeConversationSentenceInRedis(conversationId);
        return true;
    }

    private Sentence toSentence(RedisSentenceDto dto) {
        return Sentence.builder()
                .conversation(conversationRepositoty.findByVivoxId(dto.getConversationId()).get(0))
                .member(memberRepository.find(dto.getMemberId()))
                .sentence(dto.getSentence())
                .dateTime(dto.getDateTime())
                .build();
    }

    public boolean removeConversationSentenceInRedis(String conversationId) {
        return redisTemplate.delete(KEY_CONVERSATION_SENTENCE + conversationId);
    }
}
