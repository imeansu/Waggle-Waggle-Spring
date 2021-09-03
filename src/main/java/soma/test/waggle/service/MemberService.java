package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public OnlineMemberResponseDto getOnlineMembers() {
        List<MemberInfoRequestDto> followingMembers = memberRepository.getOnlineFollowingMembers().stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());

        List<MemberInfoRequestDto> onlineMembers = memberRepository.getOnlineMembers().stream()
                .map(MemberInfoRequestDto::of)
                .filter(m -> !followingMembers.contains(m))
                .collect(Collectors.toList());

        return OnlineMemberResponseDto.builder()
                .onlineFollowingMemberSize(followingMembers.size())
                .onlineFollowingMembers(followingMembers)
                .onlineMemberSize(onlineMembers.size())
                .onlineMembers(onlineMembers)
                .build();
    }

    public CreateResponseDto createFollowing(Long followedUserId) {
        Following following = Following.builder()
                .followingMember(memberRepository.find(SecurityUtil.getCurrentMemberId()))
                .followedMember(memberRepository.find(followedUserId))
                .dateTime(LocalDateTime.now())
                .build();
        if(memberRepository.createFollowing(following)){
            return new CreateResponseDto("ok");
        } else {
            return new CreateResponseDto("blocked");
        }
    }

    public CreateResponseDto createBlocking(Long blockedUserId) {
        Blocking blocking = Blocking.builder()
                .blockingMember(memberRepository.find(SecurityUtil.getCurrentMemberId()))
                .blockedMember(memberRepository.find(blockedUserId))
                .dateTime(LocalDateTime.now())
                .build();
        if(memberRepository.createBlocking(blocking)){
            return new CreateResponseDto("ok");
        } else {
            return new CreateResponseDto("fail");
        }
    }

    public MemberListDto getFollowing(Long userId) {
        List<MemberInfoRequestDto> members = memberRepository.findFollowingWho(userId).stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    public MemberListDto getFollower(Long userId) {
        List<MemberInfoRequestDto> members = memberRepository.findWhoIsFollower(userId).stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }
}
