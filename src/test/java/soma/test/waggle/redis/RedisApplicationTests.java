package soma.test.waggle.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisApplicationTests {

    @Autowired RedisTemplate redisTemplate;

    @Test
    public void contextLoads(){

    }

    @AfterEach
    public void tearDown(){
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        redisTemplate.delete("9");
        redisTemplate.delete("5");
        redisTemplate.delete("1");
    }

    @Test
    public void redisConnectionTest() {
        final String key = "1";
        final String data = "1";

        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data);

        final String result = valueOperations.get(key);
        assertEquals(data, result);

        redisTemplate.delete("9");
        redisTemplate.delete("5");
        redisTemplate.delete("1");
    }
}
