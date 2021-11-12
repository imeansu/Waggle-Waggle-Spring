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
    @GetMapping(value = "/subscribe/{memberId}", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId, @PathVariable String memberId){
        return notificationServiceImpl.subscribe(memberId, lastEventId);
    }

    private final ProduceService produceService;
    // test 용  test////
    //
    @GetMapping("/pub")
    public ResponseEntity<Object> test(){
        produceService.produceMessage(new TopicRequestMessage(Arrays.asList("The fact that squid game most likely took inspiration from WWH’s jacket at 5:00 is epic lol", "How is this squid cooked prior to preparation", "Cutting squid so perfectly thin takes professional skills to achieve.  Great job"), Arrays.asList("1")));
        return ResponseEntity.ok("success");
    }

}
