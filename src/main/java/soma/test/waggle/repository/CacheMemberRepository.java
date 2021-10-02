package soma.test.waggle.repository;

import soma.test.waggle.redis.repository.RedisMemberDto;

import java.util.Set;

public interface CacheMemberRepository {

    public boolean addMemberInRedis(RedisMemberDto redisMemberDto, String conversationId);

    public boolean removeMemberInRedis(RedisMemberDto redisMemberDto, String conversationId);

    public Set<RedisMemberDto> findByConversationId(String conversaionId);

    public boolean removeConversationMemberInRedis(String conversationId);

}
