package soma.test.waggle.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.service.ProduceService;

import javax.annotation.Resource;

//@Service
@RequiredArgsConstructor
public class RedisProduceService implements ProduceService{

    private static final String MESSAGE_QUEUE_NAME = "topic-queue";

    private final RedisTemplate redisTemplate;
    @Resource(name = "redisTemplateForProduce")
    private ListOperations<String, String> listOperations;

    @Override
    public void produceMessage(TopicRequestMessage topicRequestMessage) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        // mapper로 직접 string으로 변환해준 다음 string으로 저장
        try {
            json = mapper.writeValueAsString(topicRequestMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        listOperations.rightPush(MESSAGE_QUEUE_NAME, json);
    }
}
