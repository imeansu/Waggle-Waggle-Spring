package soma.test.waggle.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.entity.TopicResponseMessage;

public interface NotificationService {

    public SseEmitter subscribe(String memberId, String lastEventId);
    public void sendToClient(SseEmitter emitter, String id, Object data);
    public SseEmitter findById(String id);

    public void sendTopic(TopicResponseMessage topicResponseMessage);
}
