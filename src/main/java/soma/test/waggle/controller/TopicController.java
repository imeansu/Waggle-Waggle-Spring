package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.service.TopicServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topic")
public class TopicController {

    private final TopicServiceImpl topicServiceImpl;

    @PostMapping("/{conversationId}")
    public ResponseEntity<String> newTopic(@PathVariable String conversationId, @RequestBody TopicRequestMessage topicRequestMessage){
        topicRequestMessage.setConversationId(conversationId);
        topicServiceImpl.publishTopic(topicRequestMessage);
        return ResponseEntity.ok("ok");
    }

}
