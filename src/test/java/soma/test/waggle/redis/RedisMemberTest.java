package soma.test.waggle.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import soma.test.waggle.redis.repository.RedisMemberDto;
import soma.test.waggle.redis.repository.RedisMemberRepository;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisMemberTest {

    @Autowired RedisMemberRepository redisMemberRepository;
    @Autowired RedisTemplate redisTemplate;

    @AfterEach
    public void tearDownAfterClass(){
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushAll();
                return null;
            }
        });
    }

    @Test
    public void 멤버대화에참여(){
        RedisMemberDto redisMemberDto = createRedisMemberDto(1L);
        redisMemberRepository.addMemberInRedis(redisMemberDto, "conversation1");
        Iterator<RedisMemberDto> memberDtos = redisMemberRepository.findByConversationId("conversation1").iterator();
        assertThat(memberDtos.next().getMemberId()).isEqualTo(redisMemberDto.getMemberId());

    }

    @Test
    public void 키워드전달을위한대화별참여멤버조회(){
        RedisMemberDto redisMemberDto1 = createRedisMemberDto(1L);
        RedisMemberDto redisMemberDto2 = createRedisMemberDto(2L);
        redisMemberRepository.addMemberInRedis(redisMemberDto1, "conversation1");
        redisMemberRepository.addMemberInRedis(redisMemberDto2, "conversation1");
        Set<RedisMemberDto> memberDtos = redisMemberRepository.findByConversationId("conversation1");
        assertThat(memberDtos.size()).isEqualTo(2);

    }

    private RedisMemberDto createRedisMemberDto(Long memberId) {
        return new RedisMemberDto(memberId, LocalDateTime.now());
    }

    @Test
    public void 멤버대화에서나감(){
        RedisMemberDto redisMemberDto1 = createRedisMemberDto(1L);
        RedisMemberDto redisMemberDto2 = createRedisMemberDto(2L);
        redisMemberRepository.addMemberInRedis(redisMemberDto1, "conversation1");
        redisMemberRepository.addMemberInRedis(redisMemberDto2, "conversation1");

        redisMemberRepository.removeMemberInRedis(redisMemberDto1, "conversation1");

        Iterator<RedisMemberDto> memberDtos = redisMemberRepository.findByConversationId("conversation1").iterator();
        assertThat(memberDtos.next().getMemberId()).isEqualTo(redisMemberDto2.getMemberId());

    }
}
