package soma.test.waggle.repository;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    public SseEmitter save(String id, SseEmitter emitter);

    public SseEmitter findById(String id);

    public void deleteById(String id);
}
