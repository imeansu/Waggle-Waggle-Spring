package soma.test.waggle.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.redis.repository.RedisSentenceRepository;
import soma.test.waggle.repository.*;
import soma.test.waggle.type.OnStatusType;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class WorldRoomServiceTest {

    @Autowired WorldRoomService worldRoomService;
    @Autowired WorldRoomRepository worldRoomRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntranceRoomRepository entranceRoomRepository;
    @Autowired EntityManager em;
    @Autowired ConversationRepositoty conversationRepositoty;
    @Autowired SentenceRepository sentenceRepository;
    @Autowired RedisSentenceRepository redisSentenceRepository;

    @Test
    public void pathCreate(){
        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        worldRoomService.pathCreateOrClose(new PhotonRoomIdDto(worldRoom.getId()), OnStatusType.Y);

        assertThat(worldRoomRepository.findById(worldRoom.getId()).get().getOnStatus())
                .isEqualTo(OnStatusType.Y);
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

    private void securityContext(String id) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(id, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    // conversation DB에 저장 안하므로 폐기
//    @Test
    public void pathJoinAndLeave(){

        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        member = memberRepository.findByEmail("dgxc@vkdl.com").get();
        securityContext(Long.toString(member.getId()));

        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getWorldRoom()).isEqualTo(worldRoom);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatusType.Y);
        assertThat(worldRoom.getPeople()).isEqualTo(1);

        worldRoomService.pathLeave(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getIsLast()).isEqualTo(OnStatusType.N);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatusType.N);
        assertThat(worldRoom.getPeople()).isEqualTo(0);


    }

    // conversation DB에 저장 안하므로 폐기
//    @Test
    public void pathEvent(){
        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        member = memberRepository.findByEmail("dgxc@vkdl.com").get();
        securityContext(Long.toString(member.getId()));

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