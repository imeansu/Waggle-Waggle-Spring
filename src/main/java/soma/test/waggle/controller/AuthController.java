package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.MemberInfoDtoBasedOnFirebase;
import soma.test.waggle.dto.MemberResponseDto;
import soma.test.waggle.dto.TokenDto;
import soma.test.waggle.dto.TokenRequestDto;
import soma.test.waggle.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase){
        return ResponseEntity.ok(authService.signup(memberInfoDtoBasedOnFirebase));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase) {
        return ResponseEntity.ok(authService.login(memberInfoDtoBasedOnFirebase));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }

}
