package soma.test.waggle.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.entity.Member;
import soma.test.waggle.repository.MemberRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
/**
 *      1. UserDetailService 인터페이스를 구현한 클래스
 *      2. loadUserByUsername 메소드를 오버라이드 -> 여기서 넘겨받은 userDetails 와 Authentication 의 패스워드르 비교하고 검증하는 로직을 처리
 *      3. DB 에서 username 을 기반으로 값을 가져오기 때문에 아이디 존재 여부도 자동으로 검증
 *      4. createUserDetails 에서 반환하는 UserDetails 에 memberId 를 넣었기 때문에 Authentication 객체에 memberId가 저장되고
 *          TokenProvider 에서 이를 가지고 jwt 를 만들기 때문에 security context 에서 우리가 뽑아 사용할 수 있음
 * */
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(()-> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthorityType().toString());

        return new User(
                String.valueOf(member.getId()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
