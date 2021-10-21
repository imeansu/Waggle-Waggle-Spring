package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.entity.TopicResponseMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageQueueServiceImpl implements MessageQueueService{

    private final ProduceService produceService;

    @Override
    public List<String> generateMessageQueue(Long memberId, List<String> sentences) {
        produceService.produceMessage(TopicRequestMessage.builder()
                .members(new ArrayList<>(Arrays.asList(Long.toString(memberId))))
                .sentences(sentences)
                .build());
        return null;
    }
}
