package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.CustomSseEmitter;

import javax.annotation.Resource;

/**
 * SseEmitter를 redis에 저장하는 repository class
 * 추후 패키지 리팩토링 필요
 */

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryRedisImp implements EmitterRepository{

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 10;

    @Autowired
    @Qualifier("redisTemplateForSseEmitter")
    private final RedisTemplate redisTemplateForSseEmitter;

    @Resource(name="redisTemplateForSseEmitter")
    private ValueOperations<String, CustomSseEmitter> valueOperations;


    public CustomSseEmitter save(String id, CustomSseEmitter emitter) {
        valueOperations.set(id, emitter, DEFAULT_TIMEOUT);
        return emitter;
    }

    public CustomSseEmitter findById(String id){
        return (CustomSseEmitter) valueOperations.get(id);
    }

    public void deleteById(String id) {
        redisTemplateForSseEmitter.delete(id);
    }


}
