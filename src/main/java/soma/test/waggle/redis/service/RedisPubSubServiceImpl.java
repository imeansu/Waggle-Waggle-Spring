package soma.test.waggle.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import soma.test.waggle.redis.entity.TopicRequestMessage;
import soma.test.waggle.redis.pubsub.RedisPublisher;
import soma.test.waggle.redis.pubsub.RedisSubscriber;
import soma.test.waggle.repository.CacheMemberRepository;
import soma.test.waggle.service.PubService;
import soma.test.waggle.service.NotificationService;
import soma.test.waggle.service.SubService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisPubSubServiceImpl implements PubService, SubService {

    // topic에 발행되는 액션을 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 발행자
    private final RedisPublisher redisPublisher;
    // 구독자
    private final RedisSubscriber redisSubscriber;
    // sub 채널을 conversationId 별로 관리
    private Map<String, ChannelTopic> channels;
    // SSE 전송을 위한
    private final NotificationService notificationService;
    // topic 전달은 하나의 채널로 모두 통합
    private ChannelTopic topicChannel = new ChannelTopic("topic");
    // 대화의 현재 memberId 목록을 받아오기 위해
    private final CacheMemberRepository cacheMemberRepository;

    // 통합관리로 필요 없어짐
//    // 빈 등록 후, channel을 관리할 Map 초기화
//    @PostConstruct
//    public void init(){
//        channels = new HashMap<>();
//
//    }

    @PostConstruct
    public void initSubscribe(){
        redisMessageListener.addMessageListener(redisSubscriber, topicChannel);
    }

    @Override
    public void publishTopic(TopicRequestMessage topicRequestMessage) {
        // 대화 채널이 없다면 새로 생성
        List<String> members = cacheMemberRepository.findByConversationId(topicRequestMessage.getConversationId()).stream()
                .map((member) -> member.getMemberId())
                .collect(Collectors.toList());
        topicRequestMessage.setMembers(members);
        redisPublisher.publish(topicChannel, topicRequestMessage);
    }

    @Override
    public void publishTopic(TopicRequestMessage topicRequestMessage, Long memberId){
        redisPublisher.publish(topicChannel, topicRequestMessage);
    }

    // 순환 참조로 삭제
//    // Subscriber의 onMessage에서 NotificationService로 이어줌
//    @Override
//    public void sendTopic(TopicMessage topicMessage) {
//        notificationService.sendTopic(topicMessage);
//    }


}
