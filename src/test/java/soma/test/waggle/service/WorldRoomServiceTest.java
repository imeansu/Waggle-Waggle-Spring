package soma.test.waggle.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.PhotonMemberInOutDto;
import soma.test.waggle.dto.WorldRoomCreateRequestDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.error.exception.PeopleOverLimitException;
import soma.test.waggle.repository.*;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.type.WorldMapType;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest
@Transactional
@Slf4j
public class WorldRoomServiceTest {

    @Autowired WorldRoomService worldRoomService;
    @Autowired WorldRoomRepository worldRoomRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntranceRoomRepository entranceRoomRepository;
    @Autowired EntityManager em;
    @Autowired ConversationRepositoty conversationRepositoty;
    @Autowired SentenceRepository sentenceRepository;
    @Autowired ConversationService conversationService;
    @Autowired ConversationCacheRepository conversationCacheRepository;
    @Autowired RedisTemplate redisTemplate;

    @AfterEach
    public void tearDownAfterClass(){
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushAll();
                return null;
            }
        });
    }

    @Test
    public void worldRoom_생성(){
        // given
        WorldRoomCreateRequestDto worldRoomCreateRequestDto = WorldRoomCreateRequestDto.builder()
                .name("test123")
                .map(WorldMapType.GWANGHWAMUN)
                .people(1)
                .keywords(Arrays.asList("한국어", "영어", "언어교환", "free talking"))
                .build();

        // when
        worldRoomService.createWorldRoom(worldRoomCreateRequestDto);

        // then
        assertThat(worldRoomRepository.findByName("test123").get(0).getKeywords()).isEqualTo(Arrays.asList("한국어", "영어", "언어교환", "free talking"));
    }

    @Test
    public void pathCreate_Close_반영(){
        // given
        WorldRoom worldRoom = getWorldRoom("Let's talk in English", OnStatusType.Y, Arrays.asList("한국어", "영어", "언어교환", "free talking"), 3, WorldMapType.GWANGHWAMUN);
        em.persist(worldRoom);
        PhotonRoomIdDto photonRoomIdDto = new PhotonRoomIdDto(worldRoom.getId());

        // when
        worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.Y);
        em.flush();
        em.clear();
        worldRoom = worldRoomRepository.findById(worldRoom.getId()).get();

        //then
        assertThat(worldRoom.getOnStatus()).isEqualTo(OnStatusType.Y);

        // when
        worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.N);
        em.flush();
        em.clear();
        worldRoom = worldRoomRepository.findById(worldRoom.getId()).get();

        // then
        assertThat(worldRoom.getOnStatus()).isEqualTo(OnStatusType.N);
    }


    @Test
    public void pathJoinAndLeave() throws InterruptedException {
        // given
        WorldRoom worldRoom = getWorldRoom("test123", OnStatusType.Y, Arrays.asList("한국어", "영어", "언어교환", "free talking"), 3, WorldMapType.GWANGHWAMUN);
        worldRoom.setOnStatus(OnStatusType.Y);
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        securityContext(Long.toString(member.getId()));

        // when
        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        // then
        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getWorldRoom()).isEqualTo(worldRoom);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatusType.Y);
        assertThat(worldRoom.getPeople()).isEqualTo(4);
        assertThat(conversationCacheRepository.getAdjacentNode(member.getId()).size()).isEqualTo(1);

        Thread.sleep(1000L);
        // when
        worldRoomService.pathLeave(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        // then
        assertThat(entranceRoomRepository.findByMemberId(member.getId()).getIsLast()).isEqualTo(OnStatusType.N);
        assertThat(member.getEntranceStatus()).isEqualTo(OnStatusType.N);
        assertThat(worldRoom.getPeople()).isEqualTo(3);
        assertThat(conversationCacheRepository.hasGraphKey(member.getId())).isEqualTo(false);
        log.info("member.getConversationTime() = {}", member.getConversationTime());
        assertThat(member.getConversationTime() > 0).isEqualTo(true);

    }

    @Test
    public void 월드룸_인원제한_초과시(){
        // given
        WorldRoom worldRoom = getWorldRoom("test123", OnStatusType.Y, Arrays.asList("한국어", "영어", "언어교환", "free talking"), 3, WorldMapType.GWANGHWAMUN);
        worldRoom.setOnStatus(OnStatusType.Y);
        worldRoom.setPeople(20);
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        securityContext(Long.toString(member.getId()));

        // when, then
        Assertions.assertThrows(PeopleOverLimitException.class, () ->
                worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()))
        );

    }

    @Test
    public void pathEvent(){
        // given
        WorldRoom worldRoom = getWorldRoom("test123", OnStatusType.Y, Arrays.asList("한국어", "영어", "언어교환", "free talking"), 3, WorldMapType.GWANGHWAMUN);
        worldRoom.setOnStatus(OnStatusType.Y);
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        Member member2 = createMember("testmin", "dgxc@vkdl.com");
        em.persist(member2);

        securityContext(Long.toString(member.getId()));

        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));
        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member2.getId()));

        // when
        worldRoomService.pathEvent(new PhotonMemberInOutDto(worldRoom.getId(), member.getId(), member2.getId(), true));
        worldRoomService.pathEvent(new PhotonMemberInOutDto(worldRoom.getId(), member2.getId(), member.getId(), true));
        PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), member.getId(), "안녕 나는 민수야");
        worldRoomService.pathEvent(photonConversationDto1);

        // then
        List<String> sentences = conversationCacheRepository.getSentences(member.getId());
        System.out.println("sentences = " + sentences);
        assertThat(sentences.get(1)).isEqualTo("안녕 나는 민수야");
        sentences = conversationCacheRepository.getSentences(member2.getId());
        assertThat(sentences.get(1)).isEqualTo("안녕 나는 민수야");

        // when
        worldRoomService.pathEvent(new PhotonMemberInOutDto(worldRoom.getId(), member.getId(), member2.getId(), false));
        worldRoomService.pathEvent(new PhotonMemberInOutDto(worldRoom.getId(), member2.getId(), member.getId(), false));
        photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), member.getId(), "안들려???");
        worldRoomService.pathEvent(photonConversationDto1);

        // then
        sentences = conversationCacheRepository.getSentences(member.getId());
        assertThat(sentences.get(2)).isEqualTo("안들려???");
        sentences = conversationCacheRepository.getSentences(member2.getId());
        assertThat(sentences.size()).isEqualTo(2);
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

    private WorldRoom getWorldRoom(String name, OnStatusType onStatusType, List<String> keywords, int people, WorldMapType map) {
        WorldRoom worldRoom = new WorldRoom();
        worldRoom.setName(name);
        worldRoom.setOnStatus(onStatusType);
        worldRoom.setKeywords(keywords);
        worldRoom.setPeople(people);
        worldRoom.setMap(map);
        return worldRoom;
    }

}