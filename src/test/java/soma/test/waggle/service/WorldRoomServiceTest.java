package soma.test.waggle.service;

//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.redis.repository.RedisSentenceRepository;
import soma.test.waggle.repository.ConversationRepositoty;
import soma.test.waggle.repository.EntranceRoomRepository;
import soma.test.waggle.repository.SentenceRepository;
import soma.test.waggle.repository.WorldRoomRepository;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorldRoomServiceTest {

    @Autowired WorldRoomService worldRoomService;
    @Autowired WorldRoomRepository worldRoomRepository;
    @Autowired EntranceRoomRepository entranceRoomRepository;
    @Autowired EntityManager em;
    @Autowired ConversationRepositoty conversationRepositoty;
    @Autowired SentenceRepository sentenceRepository;
    @Autowired RedisSentenceRepository redisSentenceRepository;

    @Test
    public void pathCreate(){
        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        worldRoomService.pathCreateOrClose(new PhotonRoomIdDto(worldRoom.getId()), OnStatus.Y);

        assertThat(worldRoomRepository.findById(worldRoom.getId()).get().getOnStatus())
                .isEqualTo(OnStatus.Y);
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

    @Test
    public void pathJoinAndLeave(){

        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getWorldRoom()).isEqualTo(worldRoom);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatus.Y);
        assertThat(worldRoom.getPeople()).isEqualTo(1);

        worldRoomService.pathLeave(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getIsLast()).isEqualTo(OnStatus.N);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatus.N);
        assertThat(worldRoom.getPeople()).isEqualTo(0);


    }

    @Test
    public void pathEvent(){
        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "안녕 나는 민수야");

        worldRoomService.pathEvent(photonConversationDto1);

        Long conversationId1 = conversationRepositoty.findByVivoxId("1234").get(0).getId();
        assertThat(redisSentenceRepository.getSentenceFromRedis("1234").get(0).getSentence()).isEqualTo("안녕 나는 민수야");

        PhotonConversationDto photonConversationDto2 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "너의 이름은 무엇이니?");

        worldRoomService.pathEvent(photonConversationDto2);

        assertThat(redisSentenceRepository.getSentenceFromRedis("1234").get(1).getSentence()).isEqualTo("너의 이름은 무엇이니?");


    }

}