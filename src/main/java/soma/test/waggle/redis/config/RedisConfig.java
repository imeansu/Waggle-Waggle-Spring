package soma.test.waggle.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import soma.test.waggle.entity.CustomSseEmitter;
import soma.test.waggle.redis.entity.TopicMessage;

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
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    // jackson LocalDateTime mapper
    @Bean public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 형식 안따르도록 설정
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime 매핑을 위해 모듈 활성화
        return mapper;
    }


    @Bean
    @Primary
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
//        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, CustomSseEmitter> redisTemplateForSseEmitter() {
        RedisTemplate<String, CustomSseEmitter> redisTemplate = new RedisTemplate<String, CustomSseEmitter>();
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setValueSerializer(new SseEmitterConverter());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return null;
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
