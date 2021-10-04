package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import soma.test.waggle.entity.CustomSseEmitter;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryMemoryImpl implements EmitterRepository{

    Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    @Override
    public SseEmitter save(String id, SseEmitter emitter) {
        sseEmitterMap.put(id, emitter);
        return emitter;
    }

    @Override
    public SseEmitter findById(String id) {
        return sseEmitterMap.get(id);
    }

    @Override
    public void deleteById(String id) {
        sseEmitterMap.remove(id);
    }
}
