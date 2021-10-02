package soma.test.waggle.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import soma.test.waggle.entity.CustomSseEmitter;
import soma.test.waggle.redis.repository.RedisMemberDto;
import soma.test.waggle.redis.repository.RedisMemberRepository;
import soma.test.waggle.repository.EmitterRepositoryRedisImpl;
import soma.test.waggle.service.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisMemberTest {

    @Autowired RedisMemberRepository redisMemberRepository;
    @Autowired RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("redisTemplateForSseEmitter")
    RedisTemplate redisTemplateForSseEmitter;
    @Autowired
    NotificationServiceImpl notificationServiceImpl;
    @Autowired
    EmitterRepositoryRedisImpl emitterRepositoryRedisImpl;

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
        return new RedisMemberDto(Long.toString(memberId));
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

//    @Test
//    public void sse_레디스_저장_후_복구_가능_테스트(){
//
//        org.springframework.web.servlet.mvc.method.annotation.SseEmitter sseEmitter1 = notificationServiceImpl.subscribe("a","");
//        Class targetType = redisTemplateForSseEmitter.getValueSerializer().getTargetType();
//        System.out.println("targetType = " + targetType);
//        sseEmitter1 = notificationServiceImpl.subscribe("b", "");
//        CustomSseEmitter findCustomSseEmitter = emitterRepositoryRedisImpl.findById("a");
//        notificationServiceImpl.sendToClient(findCustomSseEmitter, "a", "받아줘~~~~~~");
//    }

}
