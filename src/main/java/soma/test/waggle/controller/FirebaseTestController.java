package soma.test.waggle.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soma.test.waggle.dto.FirebaseTokenDto;
import soma.test.waggle.dto.MemberDto;
import soma.test.waggle.service.FirebaseService;
import soma.test.waggle.service.MemberService;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class FirebaseTestController {

    private final FirebaseService firebaseService;
    private final MemberService memberService;

    @PostMapping("/auth")
    public ResponseEntity<String> Authentication(@RequestBody FirebaseTokenDto idToken){
        try {
            System.out.println("==============start========");
            System.out.println("idToken = " + idToken);
            FirebaseToken result =  firebaseService.firebaseAuthentication(idToken);
            System.out.println("result = " + result);
            MemberDto memberDto = new MemberDto();
            memberDto.setFirebaseId(result.getUid());
            memberDto.setEmail(result.getEmail());
            memberDto.setName(result.getName());
            memberService.initMemberJoin(memberDto);
            System.out.println("==============save===========");
            return ResponseEntity.ok("sucess");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("유효하지 않은 사용자 정보입니다.");
        }
    }
}
