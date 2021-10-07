package soma.test.waggle.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.entity.TopicResponseMessage;
import soma.test.waggle.service.NotificationService;

@Service
@RequiredArgsConstructor
/**
 * sub한 topic에 메세지가 오면 실행됨
 * */
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;

    private final NotificationService notificationService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            // String으로 변환
            String body = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // TopicMessage로 변환
            TopicResponseMessage topicResponseMessage = objectMapper.readValue(body, TopicResponseMessage.class);
            // TopicMessage 보내기
            notificationService.sendTopic(topicResponseMessage);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
