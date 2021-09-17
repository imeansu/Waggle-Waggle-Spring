package soma.test.waggle.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisMemberRepository {

    private final RedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private SetOperations<String, RedisMemberDto> setOperations;

    private static final String KEY_CONVERSATION_MEMBER = "conversation:member:";

    public boolean addMemberInRedis(RedisMemberDto redisMemberDto, String conversationId){
        if (setOperations.add(KEY_CONVERSATION_MEMBER+conversationId, redisMemberDto) > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean removeMemberInRedis(RedisMemberDto redisMemberDto, String conversationId){
        if (setOperations.remove(KEY_CONVERSATION_MEMBER+conversationId, redisMemberDto) > 0){
            return true;
        } else {
            return false;
        }
    }

    public Set<RedisMemberDto> findByConversationId(String conversaionId){
        return setOperations.members(KEY_CONVERSATION_MEMBER+conversaionId);
    }

    public boolean removeConversationMemberInRedis(String conversationId) {
        return redisTemplate.delete(KEY_CONVERSATION_MEMBER + conversationId);
    }

}
