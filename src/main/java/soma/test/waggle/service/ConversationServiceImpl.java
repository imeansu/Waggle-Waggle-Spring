package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.VivoxMemberInOutDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.repository.ConversationCacheRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService{

    private final ConversationCacheRepository conversationCacheRepository;
    private final TopicService topicService;

    private static final Long SENTENCE_AMOUNT = 10L;

    // room에 처음 입장했을시, graph와 sentence list 생성
    @Override
    public void joinRoom(Long memberId) {
        conversationCacheRepository.createGraphSet(memberId);
        conversationCacheRepository.createSentenceList(memberId);
    }

    // room에서 퇴장시, graph와 sentence list 삭제
    @Override
    public void leaveRoom(Long memberId) {
        // graph에서 노드 삭제
        conversationCacheRepository.deleteGraph(memberId);

        // sentence list 삭제
        conversationCacheRepository.deleteSentence(memberId);
    }

    // sentence가 전송되었을 때,
    // 자신의 sentence list에 추가
    // 인접 노드에 sentence 추가
    // sentence S3 통합 저장을 위해 total sentence에 저장
    @Override
    public void sentence(PhotonConversationDto photonConversationDto) {
        // 자신의 sentence list에 추가 -> 자신도 인접 노드에 추가하여 한번에 토픽 추천 개수 조사
//        conversationCacheRepository.addSentence(photonConversationDto.getMemberId(), photonConversationDto.getSentence());
        // 인접 노드 조회
        Set<Long> adjacentNodes = conversationCacheRepository.getAdjacentNode(photonConversationDto.getMemberId());


        // 문장 추가
        // 문장 추가 후 SENTENCE_AMOUNT 개 문장이 넘어가면 추천 토픽 생성 로직 실행
        for (Long memberId : adjacentNodes) {
            if (conversationCacheRepository.addSentence(memberId, photonConversationDto.getSentence()) >= SENTENCE_AMOUNT){
                // 인접 노드도 조사하여 겹치는 문장이 50% 이상이면 같이 토픽추천을 받게 됨
                // 토픽 추천을 받는다는 것은 지금 같은 대화 흐름에 참여하고 있다는 뜻 -> 현재 차이를 조정해서 다음에도 같은 토픽을 받도록 한다
                /* 겹치지 않는 문장이 발생하는 경우
                    1. 다른 곳에 있다가 방금 대화에 참여하게 된 경우
                    2. 지나가는 사람
                    3. 중간 지대에 있는 사람
                    4. 다른 곳에서 대화하고 있다가 잠깐 겹쳐진 사람
                    5. 다른 곳에서 말하다가 9문장이 쌓이고 자유롭게 지나다니다가 우연히 1문장을 들어서 10문장이 된 사람
                    겹치지 않는 문장이 중요한 사람은 4번 -> 일치율이 높지 않으면 같은 대화 흐름이라고 볼 수 없음 -> 삭제해도 될까?
                    1번의 경우라면 대화 참여자들이 같은 대화 흐름을 갖는 것이 중요한데 -> 지금 정리해줘야 계속 같이 갈 수 있음
                    삭제 여부를 듣는 사람 입장에서 산출 -> 다음 번에 자기 턴으로 인접 노드들의 문장을 정리할 때 사용
                 */
                // 일단 개인별로 진행
//                conversationCacheRepository.getAdjacentNode(memberId).stream()
//                        .map((adjacentNode) -> {
//
//                        });
                sendSentenceSetToRecommendation(memberId);

            }
        }

        // total sentence에 저장
        conversationCacheRepository.addSentenceToTotal(photonConversationDto, adjacentNodes);
    }

    // 대화 set을 보내기
    @Override
    public void sendSentenceSetToRecommendation(Long memberId) {
        topicService.recommendTopic(memberId, conversationCacheRepository.getSentences(memberId));
        conversationCacheRepository.clearSentenceList(memberId);
    }

    // 자신의 영역에 사람이 들어옴
    @Override
    public void vivoxMemberIn(VivoxMemberInOutDto vivoxMemberInOutDto) {
        conversationCacheRepository.addAdjacentNode(vivoxMemberInOutDto.getSayingMemberId(), vivoxMemberInOutDto.getHearingMemberId());
    }

    // 자신의 영역에서 사람이 나감
    @Override
    public void vivoxMemberOut(VivoxMemberInOutDto vivoxMemberInOutDto) {
        conversationCacheRepository.deleteAdjacentNode(vivoxMemberInOutDto.getSayingMemberId(), vivoxMemberInOutDto.getHearingMemberId());
    }
}
