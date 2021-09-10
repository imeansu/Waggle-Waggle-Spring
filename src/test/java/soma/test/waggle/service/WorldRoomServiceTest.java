package soma.test.waggle.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.EntranceRoomRepository;
import soma.test.waggle.repository.WorldRoomRepository;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorldRoomServiceTest {

    @Autowired WorldRoomService worldRoomService;
    @Autowired WorldRoomRepository worldRoomRepository;
    @Autowired EntranceRoomRepository entranceRoomRepository;
    @Autowired EntityManager em;

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

}