package soma.test.waggle.redis.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicMessage;

@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;

    // redis에 직접 발행
    public void publish(ChannelTopic topic, TopicMessage topicMessage){
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        // mapper로 직접 string으로 변환해준 다음 string으로 저장
        try {
            json = mapper.writeValueAsString(topicMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
