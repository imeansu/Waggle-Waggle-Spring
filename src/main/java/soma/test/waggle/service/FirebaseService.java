package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.FirebaseTokenResponseDto;
import soma.test.waggle.dto.InitMemberDto;
import soma.test.waggle.dto.MemberRequestDto;
import soma.test.waggle.dto.TokenDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;
    private final MemberRepository memberRepository;
    private final AuthService authService;

    @Transactional
    public FirebaseTokenResponseDto firebaseAuthentication(String idToken) throws FirebaseAuthException {
        // idToken comes from the client app (shown above)
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                                                                .email(decodedToken.getEmail())
                                                                .name(decodedToken.getName())
                                                                .firebaseId(decodedToken.getUid())
                                                                .password(decodedToken.getUid())
                                                                .date(LocalDate.now())
                                                                .build();
        FirebaseTokenResponseDto firebaseTokenResponseDto = new FirebaseTokenResponseDto();
        if(!memberRepository.findByFirebaseId(decodedToken.getUid())) {
            authService.signup(memberRequestDto);
            firebaseTokenResponseDto.setIsNewMember("y");
        } else{
            firebaseTokenResponseDto.setIsNewMember("n");
        }
        TokenDto tokenDto = authService.login(memberRequestDto);
        firebaseTokenResponseDto.setToken(tokenDto);
        return firebaseTokenResponseDto;
    }

}
