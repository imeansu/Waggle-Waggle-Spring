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
import soma.test.waggle.entity.OnStatus;
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

    // temp를 위한 주입
    private final MemberService memberService;

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
            return ResponseEntity.badRequest().body("result: 잘못된 토큰입니다.");
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

    @GetMapping("/auth/temp")
    public TokenDto temp(){
        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                .email("gcnml0@gmail.com")
                .name("minsu kim")
                .firebaseId("dsfs3h28xyrh38ny87sghsunc93xhu")
                .password("dsfs3h28xyrh38ny87sghsunc93xhu")
                .date(LocalDate.now())
                .build();
        authService.signup(memberRequestDto);
        try {
            memberService.getMemberInfo("gcnml0@gmail.com");
        }
        catch (Exception e){
            authService.signup(memberRequestDto);
        }
        TokenDto tokenDto = authService.login(memberRequestDto);
        System.out.println("========AccessToken = " + tokenDto.getAccessToken());

        return tokenDto;
    }
}
