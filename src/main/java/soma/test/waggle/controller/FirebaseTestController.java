package soma.test.waggle.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soma.test.waggle.dto.FirebaseTokenDto;
import soma.test.waggle.service.FirebaseService;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class FirebaseTestController {

    private final FirebaseService firebaseService;

    @PostMapping("/auth")
    public ResponseEntity<String> Authentication(@RequestBody FirebaseTokenDto idToken){
        String result =  firebaseService.firebaseAuthentication(idToken);
        if (result.equals("fail")){
            return ResponseEntity.badRequest().body("유효하지 않은 사용자 정보입니다.");
        } else{
            return ResponseEntity.ok("sucess");
        }
    }
}
