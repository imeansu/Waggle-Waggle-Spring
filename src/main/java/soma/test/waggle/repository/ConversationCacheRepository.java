package soma.test.waggle.repository;

import soma.test.waggle.dto.photon.PhotonConversationDto;

import java.util.List;
import java.util.Set;

public interface ConversationCacheRepository {
    boolean createGraph(Long memberId);

    boolean createSentence(Long memberId);
    
    boolean deleteGraph(Long memberId);

    boolean deleteSentence(Long memberId);


    Long addSentence(Long memberId, String sentence);

    Set<Long> getAdjacentNode(Long memberId);

    List<String> getSentences(Long memberId);


    boolean addSentenceToTotal(PhotonConversationDto photonConversationDto, Set<Long> adjacentNodes);

    boolean addAdjacentNode(Long sayingMemberId, Long hearingMemberId);

    boolean deleteAdjacentNode(Long sayingMemberId, Long sayingMemberId1);
}
