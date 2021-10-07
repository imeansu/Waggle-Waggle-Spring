package soma.test.waggle.service;

import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.entity.TopicResponseMessage;

public interface ProduceService {

    void produceMessage(TopicRequestMessage topicRequestMessage);
}
