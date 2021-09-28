package soma.test.waggle.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import soma.test.waggle.entity.CustomSseEmitter;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, CustomSseEmitter> redisTemplateForSseEmitter() {
        RedisTemplate<String, CustomSseEmitter> redisTemplate = new RedisTemplate<String, CustomSseEmitter>();
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setValueSerializer(new SseEmitterConverter());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

//    혹시 serialize 될까 싶어서
//    public static class SseEmitterConverter implements RedisSerializer<Object>{
//
//        @Override
//        public byte[] serialize(Object o) throws SerializationException {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutput out = null;
//            try {
//                out = new ObjectOutputStream(bos);
//            } catch (IOException exception) {
//                exception.printStackTrace();
//            }
//            try {
//                out.writeObject(o);
//            } catch (IOException exception) {
//                exception.printStackTrace();
//            }
//            byte[] myBytes = bos.toByteArray();
//            return myBytes;
//        }
//
//        @SneakyThrows
//        @Override
//        public Object deserialize(byte[] myBytes) throws SerializationException {
//            ByteArrayInputStream bis = new ByteArrayInputStream(myBytes);
//            ObjectInput in = null;
//            try {
//                in = new ObjectInputStream(bis);
//            } catch (IOException exception) {
//                exception.printStackTrace();
//            }
//            Object myObject = in.readObject();
//            bis.close();
//            in.close();
//            return myObject;
//        }
//    }

}
