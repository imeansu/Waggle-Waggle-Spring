package soma.test.waggle.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import soma.test.waggle.repository.CacheMemberRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisMessageQueueServiceImplTest {

    @Autowired MessageQueueService messageQueueService;
    @Autowired CacheMemberRepository cacheMemberRepository;
    @Autowired NotificationService notificationService;

    @Test
    public void 큐에_넣으면_subscribe로_응답() throws InterruptedException {
        notificationService.subscribe("1", "");
        List<String> sentences = new ArrayList<>(Arrays.asList("dd", "ㄹㄹ", "spring에서 배달왔습니다~"));
        messageQueueService.generateMessageQueue(1L, sentences);
        Thread.sleep(1000);

    }

}