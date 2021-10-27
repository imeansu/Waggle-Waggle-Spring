package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.RefreshToken;
import soma.test.waggle.repository.FriendshipRepository;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.repository.RefreshTokenRepository;
import soma.test.waggle.type.CountryType;
import soma.test.waggle.type.FriendshipType;
import soma.test.waggle.type.LanguageType;
import soma.test.waggle.type.OnStatusType;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
@Slf4j
public class MemberServiceTest {

    // 외부 api 통합 함수 테스트를 위해 Mock 객체 생성
    // FirebaseToken은 final, default 생성자로 임의로 생성할 수 없음 -> Mock 을 통해 해결할 수 있는 방안을 찾아보아야 함
    // @InjectMocks FirebaseService firebaseService;
    // @Mock FirebaseAuth firebaseAuth;

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired FriendshipRepository friendshipRepository;
    @Autowired EntityManager em;
    @Autowired AuthService authService;

    // securityContext에 memberId 저장
    private void securityContext(String id) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(id, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    private Member createMember(String name, String email) {
        Member member1 = new Member();
        member1.setEmail(email);
        member1.setName(name);
        return member1;
    }

    // 온라인 멤버 조회시 팔로우한 멤버, 일반 멤버 구분 조회
    @Test
    public void 온라인멤버() {
        // given
        Member member1 = createMember("member1", "dsfsdfsd");
        member1.setNickname("member1");
        em.persist(member1);

        Member member2 = createMember("member2", "dsffdsf");
        member2.setNickname("member2");
        member2.setOnlineStatus(OnStatusType.Y);
        em.persist(member2);

        Member member3 = createMember("member3", "dsffdsf");
        member3.setNickname("member3");
        member3.setOnlineStatus(OnStatusType.Y);
        em.persist(member3);

        Following following = Following.builder()
                .followingMember(member1)
                .followedMember(member2)
                .dateTime(LocalDateTime.now())
                .build();
        em.persist(following);

        // member1 입장에서 조회
        securityContext(Long.toString(member1.getId()));

        // when
        OnlineMemberResponseDto dto = memberService.getOnlineMembers();
        log.info("dto = {}", dto.toString());

        // then
        assertThat(dto.getOnlineFollowingMembers().get(0).getNickName()).isEqualTo("member2");
        assertThat(dto.getOnlineMembers().stream()
                        .map(infoDto -> infoDto.getNickName())
                        .collect(Collectors.toList())
                .contains("member3")
        ).isEqualTo(true);

    }

    // 토큰 주인이 상대방을 차단하면 팔로잉 자동 삭제
    @Test
    public void 차단() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        // when
        memberService.createFollowingWithCheck(member2.getId());

        // then : following 정상 동작 확인
        Member findMember = friendshipRepository.findFollowingWho(member1.getId()).get(0);
        assertThat(findMember).isEqualTo(member2);

        // when
        securityContext(Long.toString(member2.getId()));
        memberService.createFollowingWithCheck(member1.getId());
        memberService.createBlocking(member1.getId());

        // then : 차단 정상 동작 확인 & 팔로잉 삭제
        findMember = friendshipRepository.findBlockingWho(member2.getId()).get(0);
        assertThat(findMember).isEqualTo(member1);

        List<Member> findMembers = friendshipRepository.findFollowingWho(member1.getId());
        assertThat(findMembers.size()).isEqualTo(0);

        List<Member> findMembers2 = friendshipRepository.findFollowingWho(member2.getId());
        assertThat(findMembers2.size()).isEqualTo(0);

    }

    // 팔로잉 시, 팔로우 및 팔로워 조회
    @Test
    public void 팔로잉팔로워_조회() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowingWithCheck(member2.getId());

        em.flush();
        em.clear();

        // when
        List<MemberInfoDto> follower = memberService.getWhoIsFollower(member2.getId()).getMembers();
        log.info("follower = {}", follower.toString());
        List<MemberInfoDto> following = memberService.getFollowingWho(member1.getId()).getMembers();

        // then : 팔로워 조회, 팔로우 조회
        assertThat(follower.get(0).getId()).isEqualTo(member1.getId());
        assertThat(following.get(0).getId()).isEqualTo(member2.getId());

    }

    @Test
    public void 팔로우취소() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowingWithCheck(member2.getId());

        // when
        memberService.deleteFollowing(member2.getId());
        List<MemberInfoDto> following = memberService.getFollowingWho(member1.getId()).getMembers();

        // then : 팔로우 취소됨
        assertThat(following.size()).isEqualTo(0);

    }

    @Test
    public void 차단취소() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));
        memberService.createBlocking(member2.getId());

        // when
        memberService.deleteBlocking(member2.getId());

        // then
        List<MemberInfoDto> blocking2 = memberService.getBlockingWho(member1.getId()).getMembers();
        assertThat(blocking2.size()).isEqualTo(0);

    }

    // 컬렉션 연관관계 LAZY 로딩 확인
    @Test
    public void LAZY로딩() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowingWithCheck(member2.getId());

        // 영속성 컨텍스트 비우기
        em.flush();
        em.clear();
        log.info("==========em.clear===========");

        Member findMember = memberRepository.findByName("member1").get(0);
        log.info("==========findMember===========");

        // then : 쿼리 나가야 하는 시점
        log.info("followings: ", findMember.getFollowings());
        Following following = findMember.getFollowings().get(0);
        log.info("following = " + following.getFollowingMember());
        assertThat(following.getFollowingMember().getId()).isEqualTo(member1.getId());

    }


    // 다른 멤버 조회시 관계 정보를 포함하여 제공
    @Test
    public void 관계정보포함Dto() {
        // given
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        CommandResponseDto d = memberService.createFollowingWithCheck(member2.getId());
        List<MemberInfoDto> members = memberService.getFollowingWho(member1.getId()).getMembers();

        em.flush();
        em.clear();

        // when
        MemberInfoDto findMember = memberService.getMemberInfoWithFriendship(member2.getId());

        // then
        assertThat(findMember.getFriendship()).isEqualTo(FriendshipType.FOLLOW);
    }

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void 로그아웃시_refresh_token_삭제() {
        // given
        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = MemberInfoDtoBasedOnFirebase.builder()
                                                                                    .email("gcnml0@gmail.com")
                                                                                    .name("minsu kim")
                                                                                    .firebaseId("dsfs3h28xyrh38ny87sghsunc93xhu")
                                                                                    .password("dsfs3h28xyrh38ny87sghsunc93xhu")
                                                                                    .date(LocalDate.now())
                                                                                    .build();
        // 회원 가입
        authService.signup(memberInfoDtoBasedOnFirebase);
        // 로그인 - token 발급
        TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
        Member member = memberRepository.findByEmail(memberInfoDtoBasedOnFirebase.getEmail()).get();
        // refresh token 조회
        Optional<RefreshToken> token = refreshTokenRepository.findByKey(Long.toString(member.getId()));
        assertThat(token.isPresent()).isEqualTo(true);

        // when
        securityContext(Long.toString(member.getId()));
        memberService.logout();

        // then
        token = refreshTokenRepository.findByKey(Long.toString(member.getId()));
        assertThat(token.isEmpty()).isEqualTo(true);
    }

    @Test
    public void 멤버_탈퇴() {
        // given
        Member member = createMember("member1", "abc@gmail.com");
        memberRepository.save(member);
        em.flush();
        em.clear();
        Member findMember = memberRepository.findByEmail("abc@gmail.com").get();
        assertThat(findMember.getName()).isEqualTo("member1");
        securityContext(Long.toString(findMember.getId()));

        // when
        memberRepository.deleteMember();

        // then
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            memberRepository.findByEmail("abc@gmail.com");
        });

    }

    @Test
    public void 기본_관심사_리스트() {
        // given
        // when
        List<String> basicInterests = memberService.getInterestList().getInterests();
        // then
        assertThat(basicInterests.size()).isEqualTo(6);
        log.info("basicInterests = " + basicInterests);
    }

    // 닉네임 중복 체크
    @Test
    public void 닉네임_중목_체크() {
        boolean check1 = memberService.nicknameCheck("imeansu");
        boolean check2 = memberService.nicknameCheck("minsu");
        assertThat(check1).isEqualTo(true);
        assertThat(check2).isEqualTo(true);
    }

    // FirebaseToken을 대체할 Mock token
    // FirebaseToken의 생성자가 default로 생성 불가, final로 선언되어 상속 불가
    // 다른 방법 모색
    class MockDecodedToken {
        String uid;
        String name;
        String email;

        MockDecodedToken(Map<String, String> claims) {
            this.uid = claims.get("sub");
            this.email = claims.get("name");
            this.name = claims.get("name");
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    @Test
    public void 회원가입() throws FirebaseAuthException {
        Map<String, String> claims = new HashMap<>() {{
            put("sub", "sdfdsf");
            put("name", "minsu");
            put("email", "gdkfsi@fhid.com");
        }};
        MockDecodedToken decodedToken = new MockDecodedToken(claims);

//        doReturn(new MockDecodedToken(claims)).when(firebaseAuth).verifyIdToken(any(String.class));
        MemberJoinRequestDto memberJoinRequestDto = MemberJoinRequestDto.builder()
                .firebaseToken("eyJhbGciOiJSUzI1NiIsImtpZCI6ImYwNTM4MmFlMTgxYWJlNjFiOTYwYjA1Yzk3ZmE0MDljNDdhNDQ0ZTciLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoibWluc3Uga2ltIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdqdUpZdURzRWFmT3ZEZWFiTGlGeV9VZExJWV9zSTVVb2FkTlVXUUdnPXM5Ni1jIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3NwZWFrLXdvcmxkIiwiYXVkIjoic3BlYWstd29ybGQiLCJhdXRoX3RpbWUiOjE2MzQxMjM2ODIsInVzZXJfaWQiOiJCSzVnQWFMR1N4ZzZLY2dRb3JwQ01RY2ZjQ0EyIiwic3ViIjoiQks1Z0FhTEdTeGc2S2NnUW9ycENNUWNmY0NBMiIsImlhdCI6MTYzNDEyMzY4MiwiZXhwIjoxNjM0MTI3MjgyLCJlbWFpbCI6Imdjbm1sMEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJnb29nbGUuY29tIjpbIjExNzM2NjcxOTAwMTM1NjM1NjQ3MSJdLCJlbWFpbCI6WyJnY25tbDBAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.R9c1kiHaoBQ7f1Bn2vGV9vip0HJCaa6N21cjnc9Dq-gurX0FwpYLolHSAQQJJ9BQnboJ_S9Ol6mN8ueVW0DM7WGncv0_jmW4ut3belfDJgr2WR1fvWsfCgGDs0A7f9RTQ3fVZy4qiOsvFZv8z94WPo14nie52Rd0nqhprrHR1yY_GpCFQdn-O5ToL2rMV6Hj2wQtccUKvs1njqbOZ4sszuoShB2o-EIBisz5i6bdDNAsqU-7cfT2gLJ1nvMHVTfvsxz8O3hUz8jmSN9rom4xpXRM-RTJ4mrZVvuXDsu0uYAImiQd8EFaEy-UBtNQvADu67pcHlzreYvFEL8ZRT-LwA")
                .nickname("imeansu")
                .country(CountryType.KOREA)
                .language(LanguageType.KOREAN)
                .introduction("dfsdfsd")
                .interests(new ArrayList<>(Arrays.asList("K-POP", "축구")))
                .build();

        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = MemberInfoDtoBasedOnFirebase.builder()
                .email(decodedToken.getEmail())
                .name(decodedToken.getName())
                .firebaseId(decodedToken.getUid())
                .password(decodedToken.getUid())
                .date(LocalDate.now())
                .build();

        // 일단 가입 시키고, 추가 정보 입력 (security context에 저장 해야 함)
        authService.signup(memberInfoDtoBasedOnFirebase);

        // jwt 반환을 위한 dto
        FirebaseResponseDto firebaseResponseDto = new FirebaseResponseDto();
        // member Id 가져오기
        Long memberId = memberRepository.getMemberIdByFirebaseId(memberInfoDtoBasedOnFirebase.getFirebaseId());
        firebaseResponseDto.setMemberId(memberId);

        // security Context에 memberId 저장
        securityContext(Long.toString(memberId));

        firebaseResponseDto.setIsNewMember("n");
        // token 생성하기
        TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
        firebaseResponseDto.setToken(tokenDto);

        // 추가 정보 입력
        memberService.putMemberInfo(memberJoinRequestDto.toMemberInfoRequestDto());

        MemberInfoDto memberInfo = memberService.getMemberInfoWithFriendship(firebaseResponseDto.getMemberId());
        System.out.println("memberInfo = " + memberInfo.getInterests());
        assertThat(memberInfo.getInterests().contains("K-POP")).isEqualTo(true);

    }

    // 관심사 수정할 경우
    @Test
    public void 관심사_추가_삭제_중복방지() {
        // given
        Member member = createMember("minsu", "dsfd@fdljkdf.com");
        memberRepository.save(member);
        Member findMemberM = memberRepository.findByEmail("dsfd@fdljkdf.com").get();
        Long memberId = findMemberM.getId();
        System.out.println("memberId = " + memberId);
        securityContext(Long.toString(memberId));
        MemberInfoDto findMember = memberService.getMemberInfoWithFriendship(memberId);

        // when
        List<String> interestString = new ArrayList<>(Arrays.asList("K-POP", "스포츠", "IT"));
        findMember.setInterests(interestString);
        memberService.putMemberInfo(findMember);
        interestString = new ArrayList<>(Arrays.asList("K-POP", "스포츠"));
        findMember.setInterests(interestString);
        memberService.putMemberInfo(findMember);
        interestString = new ArrayList<>(Arrays.asList("K-POP", "스포츠", "한국어"));
        findMember.setInterests(interestString);
        memberService.putMemberInfo(findMember);
        em.flush();
        em.clear();

        // then
        MemberInfoDto dto = memberService.getMemberInfoWithFriendship(memberId);
        System.out.println(dto.toString());
        for (String interest : dto.getInterests()) {
            System.out.println("interest " + interest);
        }
        assertThat(dto.getInterests().size()).isEqualTo(3);

    }
}