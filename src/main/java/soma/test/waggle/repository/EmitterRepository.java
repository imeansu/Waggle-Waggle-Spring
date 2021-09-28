package soma.test.waggle.repository;

import soma.test.waggle.entity.CustomSseEmitter;

public interface EmitterRepository {

    public CustomSseEmitter save(String id, CustomSseEmitter emitter);

    public CustomSseEmitter findById(String id);

    public void deleteById(String id);
}
