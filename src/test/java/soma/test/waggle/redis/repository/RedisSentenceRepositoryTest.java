package soma.test.waggle.redis.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.entity.Conversation;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.WorldRoom;
import soma.test.waggle.repository.ConversationRepositoty;
import soma.test.waggle.repository.EntranceRoomRepository;
import soma.test.waggle.repository.SentenceRepository;
import soma.test.waggle.repository.WorldRoomRepository;
import soma.test.waggle.service.WorldRoomService;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RedisSentenceRepositoryTest {

    @Autowired WorldRoomService worldRoomService;
    @Autowired WorldRoomRepository worldRoomRepository;
    @Autowired EntranceRoomRepository entranceRoomRepository;
    @Autowired EntityManager em;
    @Autowired ConversationRepositoty conversationRepositoty;
    @Autowired SentenceRepository sentenceRepository;
    @Autowired RedisSentenceRepository redisSentenceRepository;
    @Autowired RedisTemplate redisTemplate;

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

    // conversation DB에 저장 안하므로 폐기
//    @Test
    public void 마이그레이션(){
        WorldRoom worldRoom = createWorldRoom("Hi! Let's talk!");
        em.persist(worldRoom);

        Member member = createMember("minsu", "dgxc@vkdl.com");
        em.persist(member);

        worldRoomService.pathJoin(new PhotonMemberDto(worldRoom.getId(), member.getId()));

        PhotonConversationDto photonConversationDto1 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "안녕 나는 민수야");

        worldRoomService.pathEvent(photonConversationDto1);

        PhotonConversationDto photonConversationDto2 = new PhotonConversationDto(worldRoom.getId(), "1234", member.getId(), "너의 이름은 무엇이니?");

        worldRoomService.pathEvent(photonConversationDto2);

        redisSentenceRepository.migrationSentencetoDB("1234");

        Conversation conversation = conversationRepositoty.findByVivoxId("1234").get(0);
        assertThat(sentenceRepository.findByConversation(conversation).get(0).getSentence()).isEqualTo("안녕 나는 민수야");
        assertThat(sentenceRepository.findByConversation(conversation).get(1).getSentence()).isEqualTo("너의 이름은 무엇이니?");
        assertThat(redisTemplate.hasKey("1234")).isEqualTo(false);
    }

}