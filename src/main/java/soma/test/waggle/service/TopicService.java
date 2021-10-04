package soma.test.waggle.service;

import soma.test.waggle.redis.entity.TopicMessage;

import java.util.List;

public interface TopicService {

    public void publishTopic(TopicMessage topicMessage);

    void recommendTopic(Long memberId, List<String> sentences);
}
