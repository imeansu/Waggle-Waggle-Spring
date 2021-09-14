package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Conversation;
import soma.test.waggle.entity.Sentence;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class SentenceRepository {

    private final EntityManager em;
    private final ConversationRepositoty conversationRepositoty;

    public Sentence save(Sentence sentence){
        em.persist(sentence);
        return sentence;
    }

    public Sentence findById(Long id){
        return em.find(Sentence.class, id);
    }

    public Sentence findByConversationId(Long id){
        Conversation conversation = conversationRepositoty.findById(id);
        return em.createQuery(
                "select s from Sentence s" +
                        " where s.conversation = :conversation", Sentence.class)
                .setParameter("conversation", conversation)
                .getResultList()
                .get(0);
    }
}
