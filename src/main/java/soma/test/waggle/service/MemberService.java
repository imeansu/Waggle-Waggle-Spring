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

    public MemberInfoRequestDto findMemberById(Long memberId) {
        return this.setFriendship(memberRepository.find(memberId));
    }

    /**
     * 토큰 주인 ID의 멤버 정보 수정
     * */
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

    /**
    * 팔로잉할 사람의 ID 값만 넘겨주면 토큰 주인 ID로 팔로우 등록
    * */
    @Transactional
    public CommandResponseDto createFollowing(Long followedMemberId) {
        Following following = Following.builder()
                .followingMember(memberRepository.find(SecurityUtil.getCurrentMemberId()))
                .followedMember(memberRepository.find(followedMemberId))
                .dateTime(LocalDateTime.now())
                .build();
        if(memberRepository.createFollowing(following)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("blocked");
        }
    }

    @Transactional
    public CommandResponseDto deleteFollowing(Long followedMemberId) {
        if(memberRepository.deleteFollowing(followedMemberId)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no following");
        }
    }

    @Transactional
    public CommandResponseDto createBlocking(Long blockedMemberId) {
        Blocking blocking = Blocking.builder()
                .blockingMember(memberRepository.find(SecurityUtil.getCurrentMemberId()))
                .blockedMember(memberRepository.find(blockedMemberId))
                .dateTime(LocalDateTime.now())
                .build();
        if(memberRepository.createBlocking(blocking)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("fail");
        }
    }

    @Transactional
    public CommandResponseDto deleteBlocking(Long blockedMemberId) {
        if(memberRepository.deleteBlocking(blockedMemberId)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no blocking");
        }
    }

    public MemberListDto getBlockingWho(Long memberId){
        List<MemberInfoRequestDto> members = memberRepository.findBlockingWho(memberId).stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    public MemberListDto getFollowingWho(Long memberId) {
        List<MemberInfoRequestDto> members = memberRepository.findFollowingWho(memberId).stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    public MemberListDto getWhoIsFollower(Long memberId) {
        List<MemberInfoRequestDto> members = memberRepository.findWhoIsFollower(memberId).stream()
                .map(MemberInfoRequestDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    public MemberInfoRequestDto setFriendship(Member member){
        Friendship friendship;
        Member reqMember = memberRepository.find(SecurityUtil.getCurrentMemberId());
        List<Member> followedMembers = reqMember.getFollowings().stream()
                .map(f -> f.getFollowedMember())
                .collect(Collectors.toList());
        System.out.println("followedMembers = " + reqMember.getFollowings());
        List<Member> blockedMembers = reqMember.getBlockings().stream()
                .map(b -> b.getBlockedMember())
                .collect(Collectors.toList());
        if(followedMembers.contains(member)){
            friendship = Friendship.FOLLOW;
        } else if (blockedMembers.contains(member)) {
            friendship = Friendship.BLOCK;
        } else {
            friendship = Friendship.NONE;
        }
        MemberInfoRequestDto dto = MemberInfoRequestDto.of(member);
        dto.setFriendship(friendship);
        return dto;
    }

    @Transactional
    public CommandResponseDto logout() {
        if(memberRepository.deleteRefreshToken()){
            return new CommandResponseDto("ok");
        }
        else{
            return new CommandResponseDto("no refresh token");
        }
    }
}
