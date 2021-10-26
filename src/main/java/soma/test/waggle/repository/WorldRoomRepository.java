package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.entity.WorldRoom;

import javax.persistence.EntityManager;
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

    public List<WorldRoom> findAllByCriteria(OnStatusType onStatusType){
        return em.createQuery("select r from WorldRoom r where r.onStatus = :status", WorldRoom.class)
                .setParameter("status", onStatusType)
                .getResultList();
    }

    public List<WorldRoom> findByName(String name){
        return em.createQuery(
                "select wr from WorldRoom wr" +
                        " where wr.name = :name")
                .setParameter("name", name)
                .getResultList();
    }
}
