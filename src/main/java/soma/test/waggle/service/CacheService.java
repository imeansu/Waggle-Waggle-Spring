package soma.test.waggle.service;

import soma.test.waggle.redis.entity.TopicMessage;

/**
 * 캐시가 어플리케이션에게 제공해야 하는 기능들
 * */
public interface CacheService {

    // 토픽을 발행할 수 있어야 함
    public void publishTopic(TopicMessage topicMessage);

    // 순환참조 문제로 삭제
//    public void sendTopic(TopicMessage topicMessage);
}
