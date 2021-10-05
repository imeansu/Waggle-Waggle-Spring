package soma.test.waggle.repository;

import soma.test.waggle.dto.photon.PhotonConversationDto;

import java.util.List;
import java.util.Set;

public interface ConversationCacheRepository {

    boolean setTimeoutOfKey(Long memberId, long seconds);

    boolean createGraphSet(Long memberId);

    boolean createSentenceList(Long memberId);
    
    boolean deleteGraph(Long memberId);

    boolean deleteSentence(Long memberId);

    Long addSentence(Long memberId, String sentence);

    boolean clearSentenceList(Long memberId);

    Set<Long> getAdjacentNode(Long memberId);

    List<String> getSentences(Long memberId);


    boolean addSentenceToTotal(PhotonConversationDto photonConversationDto, Set<Long> adjacentNodes);

    List<String> getTotalSentence(Long roomId);

    boolean addAdjacentNode(Long sayingMemberId, Long hearingMemberId);

    boolean deleteAdjacentNode(Long sayingMemberId, Long sayingMemberId1);
}
