package soma.test.waggle.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.repository.ConversationCacheRepository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisConversationCacheRepositoryImpl implements ConversationCacheRepository {

    private final RedisTemplate redisTemplate;
    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;
    private static final String KEY_CONVERSATION_SENTENCE_MEMBERID = "conversation:sentence:memberId";
    private static final String KEY_CONVERSATION_SENTENCE_TOTAL_ROOMID = "conversation:sentence:total:roomId";

    @Resource(name = "redisTemplate")
    private SetOperations<String, Long> setOperations;
    private static final String KEY_CONVERSATION_GRAPH_MEMBERID = "conversation:graph:memberId";


    @Override
    public boolean createGraph(Long memberId) {
        return setOperations.add(KEY_CONVERSATION_GRAPH_MEMBERID+memberId, memberId) == 0 ? true : false;
    }

    @Override
    public boolean createSentence(Long memberId) {
        return listOperations.rightPush(KEY_CONVERSATION_SENTENCE_MEMBERID+memberId, null) == 0 ? true : false;
    }

    @Override
    public boolean deleteGraph(Long memberId) {
        return redisTemplate.delete(KEY_CONVERSATION_GRAPH_MEMBERID+memberId);
    }

    @Override
    public boolean deleteSentence(Long memberId) {
        return redisTemplate.delete(KEY_CONVERSATION_SENTENCE_MEMBERID+memberId);
    }

    @Override
    public Long addSentence(Long memberId, String sentence) {
        return listOperations.rightPush(KEY_CONVERSATION_SENTENCE_MEMBERID+memberId, sentence);
    }

    @Override
    public Set<Long> getAdjacentNode(Long memberId) {
        return setOperations.members(KEY_CONVERSATION_GRAPH_MEMBERID+memberId);
    }

    @Override
    public List<String> getSentences(Long memberId) {
        return listOperations.range(KEY_CONVERSATION_SENTENCE_MEMBERID+memberId, 0, -1);
    }


    // 마이그레이션 로직 추후 보강하기
    @Override
    public boolean addSentenceToTotal(PhotonConversationDto photonConversationDto, Set<Long> adjacentNodes) {
        return listOperations.rightPush(KEY_CONVERSATION_SENTENCE_TOTAL_ROOMID+photonConversationDto.getRoomId(), photonConversationDto.getSentence()) > 0 ? true : false;
    }

    @Override
    public boolean addAdjacentNode(Long sayingMemberId, Long hearingMemberId) {
        return setOperations.add(KEY_CONVERSATION_GRAPH_MEMBERID+sayingMemberId, hearingMemberId) > 0 ? true : false;
    }

    @Override
    public boolean deleteAdjacentNode(Long sayingMemberId, Long hearingMemberId) {
        return setOperations.remove(KEY_CONVERSATION_GRAPH_MEMBERID+sayingMemberId, hearingMemberId) > 0 ? true : false;
    }
}
