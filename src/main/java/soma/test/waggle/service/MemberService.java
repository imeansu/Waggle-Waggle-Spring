package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.InitMemberDto;
import soma.test.waggle.dto.MemberInfoRequestDto;
import soma.test.waggle.dto.MemberResponseDto;
import soma.test.waggle.entity.*;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.util.SecurityUtil;

import java.util.Optional;

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

    @Transactional
    public MemberInfoRequestDto putMemberInfo(MemberInfoRequestDto memberInfoRequestDto) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).get();
        if( memberInfoRequestDto.getNickName() != null){
            member.setNickName(memberInfoRequestDto.getNickName());
        }
        if( memberInfoRequestDto.getCountry() != null){
            member.setCountry(memberInfoRequestDto.getCountry());
        }
        if( memberInfoRequestDto.getLanguage() != null){
            member.setLanguage(memberInfoRequestDto.getLanguage());
        }
        if( memberInfoRequestDto.getIntroduction() != null){
            member.setIntroduction(memberInfoRequestDto.getIntroduction());
        }
        if( memberInfoRequestDto.getAvatar() != null){
            member.setAvatar(memberInfoRequestDto.getAvatar());
        }
        if( memberInfoRequestDto.getOnlineStatus() != null){
            member.setOnlineStatus(memberInfoRequestDto.getOnlineStatus());
        }
        if( memberInfoRequestDto.getEntranceStatus() != null){
            member.setEntranceStatus(memberInfoRequestDto.getEntranceStatus());
        }
        if( memberInfoRequestDto.getEntranceRoom() != null){
            member.setEntranceRoom(memberInfoRequestDto.getEntranceRoom());
        }
        return memberInfoRequestDto;
    }
}
