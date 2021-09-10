package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.EntranceRoom;
import soma.test.waggle.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EntranceRoomRepository {

    private final EntityManager em;

    public EntranceRoom save(EntranceRoom entranceRoom){
        em.persist(entranceRoom);
        return entranceRoom;
    }

    public EntranceRoom find(Long id){
        return em.find(EntranceRoom.class, id);
    }

    public EntranceRoom findByMemberId(Long memberId){
        Member member = em.find(Member.class, memberId);

        EntranceRoom entranceRoom = em.createQuery(
                "select e from EntranceRoom e" +
                        " join e.member m" +
                        " where m = :member", EntranceRoom.class)
                .setParameter("member", member)
                .getResultList()
                .get(0);
        return entranceRoom;

    }
}
