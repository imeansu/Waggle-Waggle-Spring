package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TempMessageQueueServiceForTest implements MessageQueueService{

    @Override
    public List<String> generateMessageQueue(Long memberId, List<String> sentences) {
        List<String> temp = new ArrayList<>(Arrays.asList("bts", "김치", "오징어게임"));
        return temp;
    }
}
