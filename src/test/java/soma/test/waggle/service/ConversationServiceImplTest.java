package soma.test.waggle.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import soma.test.waggle.dto.VivoxMemberInOutDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.repository.CacheMemberRepository;
import soma.test.waggle.repository.ConversationCacheRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConversationServiceImplTest {

    @Autowired ConversationService conversationService;
    @Autowired ConversationCacheRepository conversationCacheRepository;
    @Autowired NotificationService notificationService;
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

    private void securityContext(String id) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(id, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    @Test
    public void 멤버_그래프_문장_리스트_테스트(){

        // given
        // test용 security context
        securityContext("1");
        // memberId 1로 SseEmitter 객체 등록
        notificationService.subscribe("3", "");

        // room과 5명의 멤버
        Long roomId = 1224L;
        Long memberA = 1L;
        Long memberB = 2L;
        Long memberC = 3L;
        Long memberD = 4L;
        Long memberE = 5L;

        // when
        // 멤버들이 room에 들어오서 만났다 떨어졌다 반복 그리고 대화 진행
        conversationService.joinRoom(memberC);
        conversationService.joinRoom(memberA);
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberC).hearingMemberId(memberA).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberA).hearingMemberId(memberC).build());
        conversationService.joinRoom(memberD);
        conversationService.vivoxMemberOut(VivoxMemberInOutDto.builder().sayingMemberId(memberA).hearingMemberId(memberC).build());
        conversationService.vivoxMemberOut(VivoxMemberInOutDto.builder().sayingMemberId(memberC).hearingMemberId(memberA).build());
        conversationService.vivoxMemberOut(VivoxMemberInOutDto.builder().sayingMemberId(memberC).hearingMemberId(memberA).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberC).hearingMemberId(memberD).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberD).hearingMemberId(memberC).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("안녕하세요").memberId(memberC).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("처음뵙겠습니다").memberId(memberD).build());
        conversationService.joinRoom(memberE);
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberD).hearingMemberId(memberE).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberE).hearingMemberId(memberD).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("오징어 게임 보셨어요?").memberId(memberC).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberC).hearingMemberId(memberE).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberE).hearingMemberId(memberC).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("물론이죠 완전 존잼").memberId(memberD).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("오징어게임 이야기 하고 있어요?").memberId(memberE).build());
        conversationService.joinRoom(memberB);
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberA).hearingMemberId(memberB).build());
        conversationService.vivoxMemberIn(VivoxMemberInOutDto.builder().sayingMemberId(memberB).hearingMemberId(memberA).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("안녕하세요").memberId(memberA).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("안녕하세요").memberId(memberB).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("네네 E님은 오징어를 많이 닮이셨네요").memberId(memberC).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("어디살아요?").memberId(memberA).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("맞아요 목소리만 들어도 개못생겼어요").memberId(memberD).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("허허 무슨 그런 농담을").memberId(memberE).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("혹시 트와이스 신곡은 들으셨어요? 이번에 노래 죽이던데").memberId(memberC).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("저 저번에 길가다가 트와이스 멤버들 봤는데 진짜 얼굴이 주먹만해요").memberId(memberE).build());
        conversationService.sentence(PhotonConversationDto.builder().roomId(roomId).sentence("헐 대박 부럽다").memberId(memberC).build());

        // then
        // 총 문장 수, 10문장 넘은 멤버의 sentence list에 남은 문장 수
        // 만났다 떨어진 멤버를 그래프에서 삭제
        assertThat(conversationCacheRepository.getTotalSentence(roomId).size()).isEqualTo(14);
        assertThat(conversationCacheRepository.getSentences(memberC).size()).isEqualTo(2);
        assertThat(conversationCacheRepository.getSentences(memberD).size()).isEqualTo(2);
        assertThat(conversationCacheRepository.getSentences(memberE).size()).isEqualTo(9);
        assertThat(conversationCacheRepository.getAdjacentNode(memberC).contains(memberA)).isEqualTo(false);



    }

}