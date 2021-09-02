package soma.test.waggle.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soma.test.waggle.dto.*;
import soma.test.waggle.service.AuthService;
import soma.test.waggle.service.FirebaseService;
import soma.test.waggle.service.MemberService;

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
}
