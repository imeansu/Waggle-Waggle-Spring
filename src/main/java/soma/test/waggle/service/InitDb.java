package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.MemberRequestDto;
import soma.test.waggle.dto.TokenDto;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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
        private final AuthService authService;
        public void dbInit1(){

            for (int i = 1; i <= 10; i++){
                WorldRoom worldRoom = new WorldRoom();
                worldRoom.setName("world_"+i);
                worldRoom.setOnStatus(i%2==0? OnStatus.Y : OnStatus.N);
                worldRoom.setKeywords(Arrays.asList("1","2","3"));
                em.persist(worldRoom);
            }
        }

    }
}
