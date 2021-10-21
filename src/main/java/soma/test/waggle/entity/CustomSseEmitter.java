package soma.test.waggle.entity;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;

public class CustomSseEmitter extends SseEmitter implements Serializable {

    public CustomSseEmitter(Long defaultTimeout) {
        super(defaultTimeout);
    }
}
