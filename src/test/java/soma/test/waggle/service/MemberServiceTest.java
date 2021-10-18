package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
import soma.test.waggle.entity.*;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.repository.RefreshTokenRepository;
import soma.test.waggle.util.SecurityUtil;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {

    // FirebaseToken은 final, default 생성자라서 실패...
    // 외부 api 통합 함수 테스트를 위해 Mock 객체 생성
//    @InjectMocks FirebaseService firebaseService;
//    @Mock FirebaseAuth firebaseAuth;


    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired AuthService authService;
//    @AfterEach
//    void memberCheck(){
//        List<Member> members = memberRepository.findAll();
//        System.out.println("members.size() = " + members.size());
//    }

    @Test
    public void 다른사람_정보_조회(){

    }

    @Test
    public void 온라인멤버() {

        Member member1 = createMember("member1", "dsfsdfsd");
        em.persist(member1);

        Member member2 = createMember("member2", "dsffdsf");
        em.persist(member2);

        Member member3 = createMember("member2", "dsffdsf");
        member3.setOnlineStatus(OnStatus.Y);
        em.persist(member3);

        Following following = Following.builder()
                .followingMember(member1)
                .followedMember(member2)
                .dateTime(LocalDateTime.now())
                .build();
        em.persist(following);

        securityContext(Long.toString(member1.getId()));

        OnlineMemberResponseDto dto = memberService.getOnlineMembers();
        System.out.println("dto = " + dto.toString());

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
    public void 차단(){

        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowing(member2.getId());

        Member findMember = memberRepository.findFollowingWho(member1.getId()).get(0);

        assertThat(findMember).isEqualTo(member2);

        memberService.createBlocking(member2.getId());

        findMember = memberRepository.findBlockMember(member1.getId()).get(0);

        assertThat(findMember).isEqualTo(member2);

        List<Member> findMembers = memberRepository.findFollowingWho(member1.getId());
        assertThat(findMembers.size()).isEqualTo(0);

//        fail("삭제 안됨");

    }

    @Test
    public void 팔로잉팔로워(){

        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowing(member2.getId());

        em.flush();
        em.clear();

        List<MemberInfoRequestDto> follower = memberService.getWhoIsFollower(member2.getId()).getMembers();
        System.out.println("follower = " + follower.toString());
        assertThat(follower.get(0).getId()).isEqualTo(member1.getId());

        List<MemberInfoRequestDto> following = memberService.getFollowingWho(member1.getId()).getMembers();

        assertThat(following.get(0).getId()).isEqualTo(member2.getId());


    }

    @Test
    public void 팔로우취소(){
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowing(member2.getId());

        List<MemberInfoRequestDto> following = memberService.getFollowingWho(member1.getId()).getMembers();

        assertThat(following.get(0).getId()).isEqualTo(member2.getId());

        memberService.deleteFollowing(member2.getId());

        following = memberService.getFollowingWho(member1.getId()).getMembers();

        assertThat(following.size()).isEqualTo(0);

    }

    @Test
    public void 차단취소(){
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createBlocking(member2.getId());

        List<MemberInfoRequestDto> blocking1 = memberService.getBlockingWho(member1.getId()).getMembers();

        assertThat(blocking1.get(0).getId()).isEqualTo(member2.getId());

        memberService.deleteBlocking(member2.getId());

        List<MemberInfoRequestDto> blocking2 = memberService.getBlockingWho(member1.getId()).getMembers();

        assertThat(blocking2.size()).isEqualTo(0);

    }

//    @Test
    public void LAZY로딩(){
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        memberService.createFollowing(member2.getId());
        em.flush();
        em.clear();
        System.out.println("==========em.clear===========");
        Member findMember = memberRepository.find(1L);
        System.out.println("==========findMember===========");
        Following following = findMember.getFollowings().get(0);
        System.out.println("following = " + following.getFollowingMember());
        assertThat(following.getFollowingMember().getId()).isEqualTo(member1.getId());

    }

    @Test
    public void 관계정보포함Dto(){
        Member member1 = createMember("member1", "dfsdf");
        em.persist(member1);

        Member member2 = createMember("member2", "dfsdf");
        em.persist(member2);

        securityContext(Long.toString(member1.getId()));

        CommandResponseDto d = memberService.createFollowing(member2.getId());
        System.out.println("d.getStatus() = " + d.getStatus());
        List<MemberInfoRequestDto> members = memberService.getFollowingWho(member1.getId()).getMembers();
        System.out.println("members.get(0).getId() = " + members.get(0).getId());

        em.flush();
        em.clear();

        MemberInfoRequestDto following = memberService.getMemberInfo(member2.getId());

        assertThat(following.getFriendship()).isEqualTo(Friendship.FOLLOW);
    }

    @Autowired private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void deleteRefreshToken(){
        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = MemberInfoDtoBasedOnFirebase.builder()
                .email("gcnml0@gmail.com")
                .name("minsu kim")
                .firebaseId("dsfs3h28xyrh38ny87sghsunc93xhu")
                .password("dsfs3h28xyrh38ny87sghsunc93xhu")
                .date(LocalDate.now())
                .build();
        authService.signup(memberInfoDtoBasedOnFirebase);
        TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
        Member member = memberRepository.findByEmail(memberInfoDtoBasedOnFirebase.getEmail()).get();
        Optional<RefreshToken> token = refreshTokenRepository.findByKey(Long.toString(member.getId()));
        assertThat(token.isPresent()).isEqualTo(true);
        securityContext(Long.toString(member.getId()));
        memberService.logout();
        token = refreshTokenRepository.findByKey(Long.toString(member.getId()));
        assertThat(token.isEmpty()).isEqualTo(true);
    }

    private Member createMember(String name, String email) {
        Member member1 = new Member();
        member1.setEmail(email);
        member1.setName(name);
        return member1;
    }

    @Test
    public void 멤버_탈퇴(){
        // given
        Member member = createMember("member1", "abc@gmail.com");
        memberRepository.save(member);
        em.flush();
        em.clear();
        Member findMember = memberRepository.findByEmail("abc@gmail.com").get();
        assertThat(findMember.getName()).isEqualTo("member1");
        securityContext(Long.toString(findMember.getId()));

        // when
        // then
        memberRepository.deleteMember();
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            memberRepository.findByEmail("abc@gmail.com");
        });

    }

    @Test
    public void 기본_관심사_리스트(){
        // given
        // when
        List<String> basicInterests = memberService.getInterestList().getInterests();
        // then
        assertThat(basicInterests.size()).isEqualTo(6);
        System.out.println("basicInterests = " + basicInterests);
    }

    // 닉네임 중복 체크
    @Test
    public void 닉네임_중목_체크(){
        boolean check1 = memberService.nicknameCheck("imeansu");
        boolean check2 = memberService.nicknameCheck("minsu");
        assertThat(check1).isEqualTo(true);
        assertThat(check2).isEqualTo(true);
    }

//    @Getter
    class MockDecodedToken{
        String uid;
        String name;
        String email;
        MockDecodedToken(Map<String, String> claims) {
            this.uid =  claims.get("sub");
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

//        doReturn(new MockDecodedToken(claims)).when(firebaseAuth).verifyIdToken(any(String.class));
        MemberJoinRequestDto memberJoinRequestDto = MemberJoinRequestDto.builder()
                .firebaseToken("eyJhbGciOiJSUzI1NiIsImtpZCI6ImYwNTM4MmFlMTgxYWJlNjFiOTYwYjA1Yzk3ZmE0MDljNDdhNDQ0ZTciLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoibWluc3Uga2ltIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdqdUpZdURzRWFmT3ZEZWFiTGlGeV9VZExJWV9zSTVVb2FkTlVXUUdnPXM5Ni1jIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3NwZWFrLXdvcmxkIiwiYXVkIjoic3BlYWstd29ybGQiLCJhdXRoX3RpbWUiOjE2MzQxMjM2ODIsInVzZXJfaWQiOiJCSzVnQWFMR1N4ZzZLY2dRb3JwQ01RY2ZjQ0EyIiwic3ViIjoiQks1Z0FhTEdTeGc2S2NnUW9ycENNUWNmY0NBMiIsImlhdCI6MTYzNDEyMzY4MiwiZXhwIjoxNjM0MTI3MjgyLCJlbWFpbCI6Imdjbm1sMEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJnb29nbGUuY29tIjpbIjExNzM2NjcxOTAwMTM1NjM1NjQ3MSJdLCJlbWFpbCI6WyJnY25tbDBAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.R9c1kiHaoBQ7f1Bn2vGV9vip0HJCaa6N21cjnc9Dq-gurX0FwpYLolHSAQQJJ9BQnboJ_S9Ol6mN8ueVW0DM7WGncv0_jmW4ut3belfDJgr2WR1fvWsfCgGDs0A7f9RTQ3fVZy4qiOsvFZv8z94WPo14nie52Rd0nqhprrHR1yY_GpCFQdn-O5ToL2rMV6Hj2wQtccUKvs1njqbOZ4sszuoShB2o-EIBisz5i6bdDNAsqU-7cfT2gLJ1nvMHVTfvsxz8O3hUz8jmSN9rom4xpXRM-RTJ4mrZVvuXDsu0uYAImiQd8EFaEy-UBtNQvADu67pcHlzreYvFEL8ZRT-LwA")
                .nickname("imeansu")
                .country(Country.KOREA)
                .language(Language.KOREAN)
                .introduction("dfsdfsd")
                .interests(new ArrayList<>(Arrays.asList("K-POP","축구")))
                .build();
        MockDecodedToken decodedToken = new MockDecodedToken(claims);
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

        MemberInfoRequestDto memberInfo = memberService.getMemberInfo(firebaseResponseDto.getMemberId());
        System.out.println("memberInfo = " + memberInfo.getInterests());
        assertThat(memberInfo.getInterests().contains("K-POP")).isEqualTo(true);

    }

    @Test
    public void 관심사_추가_삭제_중복방지(){
        // given
        Member member = createMember("minsu", "dsfd@fdljkdf.com");
        memberRepository.save(member);
        Member findMemberM = memberRepository.findByEmail("dsfd@fdljkdf.com").get();
        Long memberId = findMemberM.getId();
        System.out.println("memberId = " + memberId);
        securityContext(Long.toString(memberId));
        MemberInfoRequestDto findMember = memberService.getMemberInfo(memberId);

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
        MemberInfoRequestDto dto = memberService.getMemberInfo(memberId);
        System.out.println(dto.toString());
        for (String interest: dto.getInterests()) {
            System.out.println("interest " + interest);
        }
        assertThat(dto.getInterests().size()).isEqualTo(3);


    }
}