package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
import soma.test.waggle.dto.OnlineMemberResponseDto;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OnlineMemberTest {

    @Autowired MemberService memberService;
    @Autowired EntityManager em;

    @Test
    public void 온라인멤버() {

        Member member1 = new Member();
        member1.setEmail("dfsf");
        member1.setName("member1");
        em.persist(member1);

        Member member2 = new Member();
        member2.setEmail("dfsf");
        member2.setName("member1");
        em.persist(member2);

        Following following = Following.builder()
                .followingMember(member1)
                .followedMember(member2)
                .dateTime(LocalDateTime.now())
                .build();
        em.persist(following);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User("1", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));

        OnlineMemberResponseDto dto = memberService.getOnlineMembers();
        System.out.println("dto = " + dto.toString());
        Assertions.assertEquals(1,1);
    }

}