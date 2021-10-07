package soma.test.waggle.redis.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.pubsub.RedisPublisher;
import soma.test.waggle.redis.repository.RedisMemberDto;
import soma.test.waggle.repository.CacheMemberRepository;
import soma.test.waggle.service.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

@SpringBootTest
//@Transactional
public class RedisTopicTest {

    @Qualifier("redisTemplate")
    @Autowired private RedisTemplate redisTemplate;
    @Autowired private NotificationService notificationService;
    @Autowired private RedisPublisher redisPublisher;
    @Autowired private TopicServiceImpl topicServiceImpl;
    @Autowired private PubService pubService;
    @Autowired private CacheMemberRepository cacheMemberRepository;
    @Autowired private WorldRoomService worldRoomService;
    @Autowired private RedisMemberService redisMemberService;
    @Autowired private EntityManager em;

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


    private WorldRoom createWorldRoom(String name) {
        WorldRoom worldRoom = new WorldRoom();
        worldRoom.setName(name);
        return worldRoom;
    }

    private Member createMember(String name, String email) {
        Member member1 = new Member();
        member1.setEmail(email);
        member1.setName(name);
        return member1;
    }

    @Test
    public void pubsub테스트(){
        // given
        // memberId 1로 SseEmitter 객체 등록
        notificationService.subscribe("1", "");
        // conversationId: con1 = {memberId: 1} 으로 redis에 저장
        redisMemberService.joinMemberInConversation(RedisMemberDto.builder()
                .memberId(Long.toString(1L))
                .build(), "con1");
        // redis에서 꺼내와보기
        Set<RedisMemberDto> set = cacheMemberRepository.findByConversationId("con1");
        System.out.println("저장 직후의 get members = " + set);

        // when
        // topic을 publish 한다
        topicServiceImpl.publishTopic(TopicRequestMessage.builder()
                .conversationId("con1")
                .sentences(new ArrayList<String>(Arrays.asList("김치", "BTS")))
                .build());
//        topicService.publishTopic(TopicMessage.builder()
//                .conversationId("con1")
//                .topics(new ArrayList<String>(Arrays.asList("왜", "되는걸까")))
//                .build());
        try{
//            Thread.sleep(1);
        } catch (Exception e){
            e.printStackTrace();
        }

        // then
        // redisSubscriber가 topic을 받아서 성공했다고 말한다
    }


}
