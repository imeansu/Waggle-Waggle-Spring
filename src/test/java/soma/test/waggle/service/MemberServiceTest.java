package soma.test.waggle.service;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.CommandResponseDto;
import soma.test.waggle.dto.MemberInfoRequestDto;
import soma.test.waggle.dto.OnlineMemberResponseDto;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    
//    @AfterEach
//    void memberCheck(){
//        List<Member> members = memberRepository.findAll();
//        System.out.println("members.size() = " + members.size());
//    }

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

        memberService.createFollowing(2L);

        em.flush();
        em.clear();

        List<MemberInfoRequestDto> follower = memberService.getWhoIsFollower(2L).getMembers();
        System.out.println("follower = " + follower.toString());
        assertThat(follower.get(0).getId()).isEqualTo(1L);

        List<MemberInfoRequestDto> following = memberService.getFollowingWho(1L).getMembers();

        assertThat(following.get(0).getId()).isEqualTo(2L);


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

    private Member createMember(String name, String email) {
        Member member1 = new Member();
        member1.setEmail(email);
        member1.setName(name);
        return member1;
    }

}