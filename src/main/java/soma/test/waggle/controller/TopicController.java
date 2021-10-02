package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.redis.entity.TopicMessage;
import soma.test.waggle.service.TopicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/{conversationId}")
    public ResponseEntity<String> newTopic(@PathVariable String conversationId, @RequestBody TopicMessage topicMessage){
        topicMessage.setConversationId(conversationId);
        topicService.publishTopic(topicMessage);
        return ResponseEntity.ok("ok");
    }

}
