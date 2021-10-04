package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicMessage;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl {

    // Publish와 관련된 서비스를 이용한다
    private final PubService pubService;

    // 토픽 발행
    public void publishTopic(TopicMessage topicMessage){
        pubService.publishTopic(topicMessage);
    }

}
