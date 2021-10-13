package soma.test.waggle.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.*;
import soma.test.waggle.service.AuthService;
import soma.test.waggle.service.FirebaseService;
import soma.test.waggle.service.MemberService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class FirebaseController {

    private final FirebaseService firebaseService;
    private final AuthService authService;
    // temp를 위한 주입
    private final MemberService memberService;

    /**
     * login or verify member
     * params: firebase token
     * returns: isNewMember = y or n and TokenDto when isNewMember is n
     * */
    @PostMapping("/firebase-token")
    public ResponseEntity<FirebaseResponseDto> loginOrVerify(@RequestBody @Valid FirebaseTokenDto firebaseTokenDto) throws FirebaseAuthException {
        FirebaseResponseDto result = firebaseService.firebaseLoginOrVerify(firebaseTokenDto.getFirebaseToken());
        return ResponseEntity.ok(result);
    }

    /**
     * sign up
     * params: "firebaseToken", "nickName", "country", "language", "introduction"
     * returns: isNewMember = y or n and TokenDto when isNewMember is n
     * */
    @PostMapping("/sign-up")
    public ResponseEntity<Object> signup(@RequestBody @Valid MemberJoinRequestDto memberJoinRequestDto) throws FirebaseAuthException {
        return ResponseEntity.ok(firebaseService.signup(memberJoinRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> reissueAccessToken(@RequestBody TokenRequestDto tokenRequestDto){
        TokenDto result = authService.reissueAccessToken(tokenRequestDto);
        return ResponseEntity.ok(new ReissueAccessTokenReponseDto(result.getAccessToken(), result.getAccessTokenExpiresIn()));
    }

    // 테스트용 토큰 발급 받기
    @GetMapping("/temp")
    public TokenDto temp(){
        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = MemberInfoDtoBasedOnFirebase.builder()
                .email("gcnml0@gmail.com")
                .name("minsu kim")
                .firebaseId("dsfs3h28xyrh38ny87sghsunc93xhu")
                .password("dsfs3h28xyrh38ny87sghsunc93xhu")
                .date(LocalDate.now())
                .build();
        try {
            memberService.getMemberInfo("gcnml0@gmail.com");
        }
        catch (Exception e){
            authService.signup(memberInfoDtoBasedOnFirebase);
        }
        TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
        System.out.println("========AccessToken = " + tokenDto.getAccessToken());

        return tokenDto;
    }
}
