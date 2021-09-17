package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Conversation;
import soma.test.waggle.entity.Sentence;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    public List<Sentence> findByConversation(Conversation conversation);
}
