package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import soma.test.waggle.redis.entity.TopicResponseMessage;
import soma.test.waggle.repository.EmitterRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{

    // SseEmitter 타임아웃 : 10분
    // 클라이언트는 연결 종료 인지 후 EventStream 자동 재생성 요청 (해야함...)
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 10;

    @Autowired
    @Qualifier("emitterRepositoryMemoryImpl")
    private EmitterRepository emitterRepository;


    public SseEmitter subscribe(String memberId, String lastEventId) {
        // 1
        // 일단 메모리에 저장 -> 로컬 캐시에 저장하는 방법 적용하기
        String id = memberId;//Long.toString(memberId);
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        log.info("subscribe: emitter = {}", emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 2
        // 503 에어를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [memberId= " + id + "]");

        // 지금은 보류
        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
//        if (!lastEventId.isEmpty()) {
//            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
//            events.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));

        return emitter;
    }

    public void sendToClient(SseEmitter emitter, String id, Object data){
        try{
            emitter.send(SseEmitter.event()
                                    .id(id)
                                    .name("sse")
                                    .data(data));
        } catch (IOException exception){
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }

    public SseEmitter findById(String id){
        return emitterRepository.findById(id);
    }

    public void sendTopic(TopicResponseMessage topicResponseMessage) {
        log.info("subscriber에 도착한 topicMessage = {}", topicResponseMessage);

        List<String> members = topicResponseMessage.getMembers();
        members.stream().forEach((member) -> {
                String id = member;
                log.info("SseEmitter 객체를 찾아서 보내기");
                log.info("memberId = {}", id);
                SseEmitter emitter = emitterRepository.findById(id);
                log.info("emitter = {}",emitter);
                if (emitter != null){
                    sendToClient(emitter, id, topicResponseMessage.getTopics());
                    log.info("topicMessage = {}", topicResponseMessage);
                    log.info("redis - sse 성공!");
            }
        });
    }
}
