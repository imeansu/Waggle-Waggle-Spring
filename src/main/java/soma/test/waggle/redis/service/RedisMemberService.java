package soma.test.waggle.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.redis.repository.RedisMemberDto;
import soma.test.waggle.redis.repository.RedisMemberRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisMemberService {

    private final RedisMemberRepository redisMemberRepository;

    @Transactional
    public void joinMemberInConversation(RedisMemberDto redisMemberDto, String conversationId){
        redisMemberRepository.addMemberInRedis(redisMemberDto, conversationId);
    }
}
