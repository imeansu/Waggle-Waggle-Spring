package soma.test.waggle.repository;

import soma.test.waggle.redis.repository.CacheMemberDto;

import java.util.Set;

public interface CacheMemberRepository {

    public boolean addMemberInRedis(CacheMemberDto cacheMemberDto, String conversationId);

    public boolean removeMemberInRedis(CacheMemberDto cacheMemberDto, String conversationId);

    public Set<CacheMemberDto> findByConversationId(String conversaionId);

    public boolean removeConversationMemberInRedis(String conversationId);

}
