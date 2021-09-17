package soma.test.waggle.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.repository.RedisMemberDto;
import soma.test.waggle.redis.repository.RedisMemberRepository;

@Service
@RequiredArgsConstructor
public class RedisMemberService {

    private final RedisMemberRepository redisMemberRepository;

    public void joinMemberInConversation(RedisMemberDto redisMemberDto, String conversationId){
        redisMemberRepository.addMemberInRedis(redisMemberDto, conversationId);
    }
}
