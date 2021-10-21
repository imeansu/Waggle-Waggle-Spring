package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicRequestMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService{

    // Publish와 관련된 서비스를 이용한다
    private final MessageQueueService messageQueueService;

    @Override
    public void recommendTopic(Long memberId, List<String> sentences) {
        List<String> topics = messageQueueService.generateMessageQueue(memberId, sentences);
    }

}
