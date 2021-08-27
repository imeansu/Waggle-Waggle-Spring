package soma.test.waggle.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.MemberDto;
import soma.test.waggle.dto.MemberResponseDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }

    // 현재 SecurityContext 에 있는 유저 정보 가져오기
    @Transactional(readOnly = true)
    public MemberResponseDto getMyInfo() {
        return memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }

    // 처음 파이어베이스 로그인 시
    @Transactional(readOnly = true)
    public MemberDto initMemberJoin(MemberDto memberDto){
        return memberRepository.save(memberDto);
    }
}
