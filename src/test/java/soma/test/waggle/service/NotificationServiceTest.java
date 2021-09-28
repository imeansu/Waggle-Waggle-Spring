package soma.test.waggle.service;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import soma.test.waggle.redis.repository.RedisMemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    RedisMemberRepository redisMemberRepository;
    @Autowired
    @Qualifier("redisTemplateForSseEmitter")
    RedisTemplate redisTemplateForSseEmitter;

    @AfterEach
    public void tearDownAfterClass(){
        redisTemplateForSseEmitter.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushAll();
                return null;
            }
        });
    }

}