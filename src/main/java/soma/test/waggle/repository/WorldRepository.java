package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.World;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorldRepository {

    private final EntityManager em;

    public World save(World world){
        em.persist(world);
        return world;
    }

    public Optional<World> findById(Long id){
        return Optional.ofNullable(em.find(World.class, id));
    }

}
