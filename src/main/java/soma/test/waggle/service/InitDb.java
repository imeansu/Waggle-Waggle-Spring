package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.MemberRequestDto;
import soma.test.waggle.dto.TokenDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.ConversationRepositoty;

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
    public void init() {
        initService.dbInit1();
//        initService.dbInit2();
    }

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final AuthService authService;
        private final ConversationRepositoty conversationRepositoty;
        private final WorldRoomService worldRoomService;

        public void dbInit1(){

            for (int i = 1; i <= 10; i++){
                WorldRoom worldRoom = new WorldRoom();
                worldRoom.setName("world_"+i);
                worldRoom.setOnStatus(i%2==0? OnStatus.Y : OnStatus.N);
                worldRoom.setKeywords(Arrays.asList("1","2","3"));
                em.persist(worldRoom);
            }
        }

        public void dbInit2(){
            WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
            em.persist(worldRoom);

            Member member = createMember("minsu", "dgxc@vkdl.com");
            em.persist(member);

            worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

            PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "안녕 나는 민수야");

            worldRoomService.pathEvent(photonConversationDto1);
        }

        private WorldRoom createWorldRoom(String name) {
            WorldRoom worldRoom = new WorldRoom();
            worldRoom.setName(name);
            return worldRoom;
        }

        private Member createMember(String name, String email) {
            Member member1 = new Member();
            member1.setEmail(email);
            member1.setName(name);
            return member1;
        }


    }
}
