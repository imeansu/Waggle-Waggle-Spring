package soma.test.waggle.service;

import soma.test.waggle.entity.CustomSseEmitter;

public interface NotificationService {

    public CustomSseEmitter subscribe(String memberId, String lastEventId);
    public void sendToClient(CustomSseEmitter emitter, String id, Object data);
    public CustomSseEmitter findById(String id);
    
}
