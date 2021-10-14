package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.entity.Interest;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.ConversationRepositoty;
import soma.test.waggle.repository.InterestRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.basicInterestInsert();
        initService.dbInit2();
    }

    @Transactional
    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final AuthService authService;
        private final ConversationRepositoty conversationRepositoty;
        private final WorldRoomService worldRoomService;
        private final InterestRepository interestRepository;

        public void dbInit1(){

            for (int i = 1; i <= 10; i++){
                WorldRoom worldRoom = new WorldRoom();
                worldRoom.setName("TEST:world_"+i);
                worldRoom.setOnStatus(i%2==0? OnStatus.Y : OnStatus.N);
                worldRoom.setKeywords(Arrays.asList("K-POP","enum말고","저놈!"));
                em.persist(worldRoom);
            }
        }

        public void dbInit2(){
            WorldRoom worldRoom = createWorldRoom("TEST:Hi! Let's talk!");
            em.persist(worldRoom);

            Member member = createMember("TEST:minsu", "TEST:dgxc@vkdl.com");
            member.setNickname("TEST:imeansu");
            em.persist(member);

            worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

            PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "안녕 나는 민수야");

            worldRoomService.pathEvent(photonConversationDto1);
        }

        public void basicInterestInsert(){
            Interest root = Interest.builder()
                    .subject("root")
                    .build();
            interestRepository.save(root);
            List<String> interests = new ArrayList<>(Arrays.asList("K-POP", "스포츠", "축구", "BTS", "한국어", "IT"));
            interests.stream()
                    .forEach((string) -> interestRepository.save(Interest.builder()
                            .parent(root)
                            .subject(string)
                            .build()));

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
