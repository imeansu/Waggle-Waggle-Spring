package soma.test.waggle.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.service.ProduceService;

@Service
@RequiredArgsConstructor
public class RabbitProduceService implements ProduceService {

    private static final String queueName = "waggle-waggle";
    private static final String directExchangeName = "waggle-exchange";
    private static final String routingKey = "waggle";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceMessage(TopicRequestMessage topicRequestMessage) {
        rabbitTemplate.convertAndSend(directExchangeName, routingKey, topicRequestMessage);
    }
}
