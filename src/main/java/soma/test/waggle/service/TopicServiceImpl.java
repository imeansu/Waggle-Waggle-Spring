package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicMessage;
import soma.test.waggle.redis.pubsub.RedisPublisher;
import soma.test.waggle.redis.service.RedisPubSubServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService{

    // Publish와 관련된 서비스를 이용한다
    private final PubService pubService;
    private final MessageQueueService messageQueueService;

    // 토픽 발행
    public void publishTopic(TopicMessage topicMessage){
        pubService.publishTopic(topicMessage);
    }

    @Override
    public void recommendTopic(Long memberId, List<String> sentences) {
        List<String> topics = messageQueueService.generateMessageQueue(memberId, sentences);
        TopicMessage topicMessage = TopicMessage.builder()
                .topics(topics)
                .members(new ArrayList<String>(Arrays.asList(Long.toString(memberId))))
                .build();
        pubService.publishTopic(topicMessage, memberId);
    }

}
