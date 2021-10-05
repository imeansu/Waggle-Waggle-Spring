package soma.test.waggle.service;

import java.util.List;

public interface MessageQueueService {

    List<String> generateMessageQueue(Long memberId, List<String> sentences);
}
