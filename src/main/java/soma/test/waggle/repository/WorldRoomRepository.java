package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorldRoomRepository {

    private final EntityManager em;

    public WorldRoom save(WorldRoom worldRoom){
        em.persist(worldRoom);
        return worldRoom;
    }

    public Optional<WorldRoom> findById(Long id){
        return Optional.ofNullable(em.find(WorldRoom.class, id));
    }

    public WorldRoom find(Long id){ return em.find(WorldRoom.class, id);}

    public List<WorldRoom> findAllByCriteria(OnStatus onStatus){
        return em.createQuery("select r from WorldRoom r where r.onStatus = :status", WorldRoom.class)
                .setParameter("status", onStatus)
                .getResultList();
    }
}
