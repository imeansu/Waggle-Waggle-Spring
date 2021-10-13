package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.*;
import soma.test.waggle.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final AuthService authService;

    public static MemberInfoDtoBasedOnFirebase getMemberRequestDto(FirebaseToken decodedToken){
        return MemberInfoDtoBasedOnFirebase.builder()
                .email(decodedToken.getEmail())
                .name(decodedToken.getName())
                .firebaseId(decodedToken.getUid())
                .password(decodedToken.getUid())
                .date(LocalDate.now())
                .build();
    }

    /**
     * 파이어베이스 로그인 시
     * 가입된 유저라면 jwt token 포함하여 전송
     * 가입되지 않은 유저라면 isNewMember: Y 만
     * */
    @Transactional
    public FirebaseResponseDto firebaseLoginOrVerify(String idToken) throws FirebaseAuthException {
        // idToken comes from the client app
        // firebase token 검증하고
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        // firebse token 기반 member dto 생성
        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = getMemberRequestDto(decodedToken);
        // firebase login 이면 토큰 채워서, verify면 isNewMember만 N으로
        FirebaseResponseDto firebaseResponseDto = new FirebaseResponseDto();
        // memberId 가져오기, 가입한 적 없으면 -1L
        Long memberId = memberRepository.getMemberIdByFirebaseId(decodedToken.getUid());
        if(memberId < 0) {
            firebaseResponseDto.setIsNewMember("y");
        } else{
            firebaseResponseDto.setIsNewMember("n");
            firebaseResponseDto.setMemberId(memberId);
            TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
            firebaseResponseDto.setToken(tokenDto);
        }
        return firebaseResponseDto;
    }

    public boolean firebaseAuthentication(String idToken) throws FirebaseAuthException {
        // idToken comes from the client app
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        return true;
    }

    public FirebaseResponseDto signup(MemberJoinRequestDto memberJoinRequestDto) throws FirebaseAuthException {
        // token verify
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(memberJoinRequestDto.getFirebaseToken());
        // token 바탕으로 추출한 정보
        MemberInfoDtoBasedOnFirebase memberInfoDtoBasedOnFirebase = getMemberRequestDto(decodedToken);
        // 가입 이력 확인
        Long memberId = memberRepository.getMemberIdByFirebaseId(memberInfoDtoBasedOnFirebase.getFirebaseId());
        if (memberId > 0){
            return new FirebaseResponseDto();
        }
        // 일단 가입 시키고, 추가 정보 입력 (security context에 저장 해야 함)
        authService.signup(memberInfoDtoBasedOnFirebase);

        // jwt 반환을 위한 dto
        FirebaseResponseDto firebaseResponseDto = new FirebaseResponseDto();
        // member Id 가져오기
        memberId = memberRepository.getMemberIdByFirebaseId(memberInfoDtoBasedOnFirebase.getFirebaseId());
        firebaseResponseDto.setMemberId(memberId);

        // security Context에 memberId 저장
        securityContext(Long.toString(memberId));

        firebaseResponseDto.setIsNewMember("n");
        // token 생성하기
        TokenDto tokenDto = authService.login(memberInfoDtoBasedOnFirebase);
        firebaseResponseDto.setToken(tokenDto);

        // 추가 정보 입력
        memberService.putMemberInfo(memberJoinRequestDto.toMemberInfoRequestDto());

        return firebaseResponseDto;
    }

    private void securityContext(String id) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(id, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }
}
