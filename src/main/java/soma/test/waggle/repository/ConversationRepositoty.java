package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Conversation;
import soma.test.waggle.exception.NoConversationException;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoty {

    private final EntityManager em;

    public Conversation save(Conversation conversation) {
        em.persist(conversation);
        return conversation;
    }

    public Conversation findById(Long id){
        return em.find(Conversation.class, id);
    }

    public Conversation findByVivoxId(String id){
        List<Conversation> findConversation = em.createQuery(
                "select c from Conversation c where vivox_id = :vivoxId", Conversation.class)
                .setParameter("vivoxId", id)
                .getResultList();
        if (findConversation.size() == 0){
            throw new NoConversationException("해당 대화가 없습니다.");
        } else{
            return findConversation.get(0);
        }
    }

}
