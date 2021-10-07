package soma.test.waggle.service;

import soma.test.waggle.redis.entity.TopicRequestMessage;

import java.util.List;

public interface TopicService {

    public void publishTopic(TopicRequestMessage topicRequestMessage);

    void recommendTopic(Long memberId, List<String> sentences);
}
