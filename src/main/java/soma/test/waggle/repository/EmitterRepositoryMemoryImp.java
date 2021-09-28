package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.CustomSseEmitter;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryMemoryImp implements EmitterRepository{

    Map<String, CustomSseEmitter> sseEmitterMap = new HashMap<>();

    @Override
    public CustomSseEmitter save(String id, CustomSseEmitter emitter) {
        sseEmitterMap.put(id, emitter);
        return emitter;
    }

    @Override
    public CustomSseEmitter findById(String id) {
        return sseEmitterMap.get(id);
    }

    @Override
    public void deleteById(String id) {
        sseEmitterMap.remove(id);
    }
}
