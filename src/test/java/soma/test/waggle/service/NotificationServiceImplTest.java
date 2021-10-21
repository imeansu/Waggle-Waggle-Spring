package soma.test.waggle.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@SpringBootTest
class NotificationServiceImplTest {

    @Autowired ConversationService conversationService;
    @Autowired NotificationService notificationService;
    @Autowired TopicService topicService;
    @Autowired RedisTemplate redisTemplate;
    @Autowired RabbitAdmin rabbitAdmin;

    @AfterEach
    public void tearDownAfterClass(){
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushAll();
                return null;
            }
        });
        rabbitAdmin.purgeQueue("waggle-waggle");
    }

    private void securityContext(String id) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(id, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    @Test
    public void python과_pub_sub_테스트() throws InterruptedException {
        // given
        // test용 security context
        securityContext("1");
        // memberId 1로 SseEmitter 객체 등록
        notificationService.subscribe("3", "");

        // when
        topicService.recommendTopic(1L, new ArrayList<>(Arrays.asList("dd", "ㄹㄹ", "spring에서 배달왔습니다~")));

        // then
        Thread.sleep(1000);
    }


}