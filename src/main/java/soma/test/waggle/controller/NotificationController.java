package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import soma.test.waggle.service.NotificationServiceImpl;

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
    public SseEmitter subscribe(@PathVariable String memberId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId ){
        return notificationServiceImpl.subscribe(memberId, lastEventId);
    }

//    @GetMapping("/subscribe/test/{memberId}")
//    public ResponseEntity<Object> publish(@PathVariable String memberId){
//        SseEmitter findCustomSseEmitter = notificationServiceImpl.findById(memberId);
//        System.out.println("findSseEmitter = " + findCustomSseEmitter);
//        notificationServiceImpl.sendToClient(findCustomSseEmitter, memberId, "이게 된다고???");
//        return ResponseEntity.ok("ok");
//    }

}
