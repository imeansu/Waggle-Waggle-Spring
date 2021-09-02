package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {initService.dbInit1();}

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1(){
            Member member = new Member();
            member.setEmail("dfsf");
            em.persist(member);
            for (int i = 1; i <= 10; i++){
                WorldRoom worldRoom = new WorldRoom();
                worldRoom.setName("world_"+i);
                worldRoom.setOnStatus(i%2==0? OnStatus.Y : OnStatus.N);
                em.persist(worldRoom);
            }
        }

    }
}
