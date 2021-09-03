package soma.test.waggle.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.service.AuthService;
import soma.test.waggle.service.FirebaseService;
import soma.test.waggle.service.MemberService;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class FirebaseTestController {

    private final FirebaseService firebaseService;
    private final AuthService authService;


    @PostMapping("/auth/firebase")
    public ResponseEntity<Object> loginOrJoin(@RequestBody FirebaseTokenDto firebaseTokenDto){

        log.info("start");
        System.out.println("==============start========");
        System.out.println("idToken = " + firebaseTokenDto);
        FirebaseTokenResponseDto result = null;
        try {
            result = firebaseService.firebaseAuthentication(firebaseTokenDto.getFirebaseToken());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("잘못된 토큰입니다.");
        }
        System.out.println("result = " + result);
        System.out.println("==============end========");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Object> reissueAccessToken(@RequestBody TokenRequestDto tokenRequestDto){
        TokenDto result = authService.reissueAccessToken(tokenRequestDto);
        return ResponseEntity.ok(new ReissueAccessTokenReponseDto(result.getAccessToken()));
    }

    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/auth/temp")
    @Transactional
    public void temp(){
        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                .email("gcnml0@gmail.com")
                .name("minsu kim")
                .firebaseId("dsfs3h28xyrh38ny87sghsunc93xhu")
                .password("dsfs3h28xyrh38ny87sghsunc93xhu")
                .date(LocalDate.now())
                .build();
        authService.signup(memberRequestDto);
        TokenDto tokenDto = authService.login(memberRequestDto);
        System.out.println("========AccessToken = " + tokenDto.getAccessToken());

        Member member1 = em.find(Member.class, 1L);

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
    }
}
