package soma.test.waggle.service;


import soma.test.waggle.dto.PhotonMemberInOutDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;

public interface ConversationService {

    void joinRoom(Long memberId);

    void leaveRoom(Long memberId);

    void sentence(PhotonConversationDto photonConversationDto);

    void sendSentenceSetToRecommendation(Long memberId);

    void vivoxMemberIn(PhotonMemberInOutDto photonMemberInOutDto);

    void vivoxMemberOut(PhotonMemberInOutDto photonMemberInOutDto);
}
