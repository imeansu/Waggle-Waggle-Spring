package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.service.NotificationServiceImpl;
import soma.test.waggle.service.ProduceService;
import soma.test.waggle.util.SecurityUtil;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationServiceImpl notificationServiceImpl;

    /**
    * 유저가 worldRoom 입장 시,
    * 재연결 요청시
    * sse 연결을 수행
    * */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId ){
        String memberId = Long.toString(SecurityUtil.getCurrentMemberId());
        return notificationServiceImpl.subscribe(memberId, lastEventId);
    }

    private final ProduceService produceService;
    // test 용  test////
    @GetMapping("/pub")
    public ResponseEntity<Object> test(){
        produceService.produceMessage(new TopicRequestMessage(Arrays.asList("dsf"), Arrays.asList("1")));
        return ResponseEntity.ok("success");
    }

}
