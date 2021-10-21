package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.error.exception.MemberNotFoundException;
import soma.test.waggle.repository.InterestMemberRepository;
import soma.test.waggle.repository.InterestRepository;
import soma.test.waggle.repository.MemberRepository;
import soma.test.waggle.type.FriendshipType;
import soma.test.waggle.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final InterestMemberRepository interestMemberRepository;

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


    /**
     * 토큰 주인을 기준으로 다른 멤버의 정보를 조회
     * Param : 정보를 조회할 MemberId
     * Throws : NullPointerException - 멤버가 존재하지 않을 때 (해당 memberId 없음)
     * */
    public MemberInfoDto getMemberInfo(Long memberId) {
        try{
            return getMemberInfo(memberRepository.find(memberId));
        } catch (Exception e){
            throw new MemberNotFoundException(e.getMessage());
        }
    }

    /**
     * 토큰 주인 ID의 멤버 정보 수정
     * */
    @Transactional
    public MemberInfoDto putMemberInfo(MemberInfoDto memberInfoDto) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).get();
        if( memberInfoDto.getNickName() != null){
            member.setNickname(memberInfoDto.getNickName());
        }
        if( memberInfoDto.getCountry() != null){
            member.setCountryType(memberInfoDto.getCountry());
        }
        if( memberInfoDto.getLanguage() != null){
            member.setLanguageType(memberInfoDto.getLanguage());
        }
        if( memberInfoDto.getIntroduction() != null){
            member.setIntroduction(memberInfoDto.getIntroduction());
        }
        if( memberInfoDto.getAvatar() != null){
            member.setAvatarType(memberInfoDto.getAvatar());
        }
        if( memberInfoDto.getOnlineStatus() != null){
            member.setOnlineStatus(memberInfoDto.getOnlineStatus());
        }
        if( memberInfoDto.getEntranceStatus() != null){
            member.setEntranceStatus(memberInfoDto.getEntranceStatus());
        }
        if( memberInfoDto.getEntranceRoom() != null){
            member.setEntranceRoom(memberInfoDto.getEntranceRoom());
        }
        if( memberInfoDto.getInterests() != null){
            // 새로운 관심사
            List<InterestMember> newInterestMemberList = toInterestMemberEntityList(memberInfoDto.getInterests());
            Set<InterestMember> interestMemberList = new HashSet<>(newInterestMemberList);
            // 이전 관심사
            Set<InterestMember> preInterestList = new HashSet<>(member.getInterests());
            // 새로운 관심사 멤버에게 적용
            member.setInterests(newInterestMemberList);
            // temp
            Set<InterestMember> tempInterestList = new HashSet<>();
            tempInterestList.addAll(interestMemberList);
            // insert 할 관심사
            interestMemberList.removeAll(preInterestList);
            // delete 할 관심사
            preInterestList.removeAll(tempInterestList);
            // 이전에 없던 관심사 저장
            interestMemberRepository.saveAll(interestMemberList);
            // 없어진 관심사 삭제
            interestMemberRepository.deleteAll(preInterestList);
        }
        return memberInfoDto;
    }

    /**
     * string list 로 받은 관심사를 InterestMember 엔티티 리스트로 변경해서 반환
     * */
    public List<InterestMember> toInterestMemberEntityList(List<String> interests){
        Member member = memberRepository.find(SecurityUtil.getCurrentMemberId());
        Map<String, Interest> interestMap = interestRepository.findInterestMap(interests);
        return interests.stream()
                .map(interest ->
                    InterestMember.builder()
                            .member(member)
                            .interest(interestMap.get(interest))
                            .build()
                )
                .collect(Collectors.toList());
    }


    public OnlineMemberResponseDto getOnlineMembers() {
        List<MemberInfoDto> followingMembers = memberRepository.getOnlineFollowingMembers().stream()
                .map(MemberInfoDto::of)
                .collect(Collectors.toList());

        List<MemberInfoDto> onlineMembers = memberRepository.getOnlineMembers().stream()
                .map(MemberInfoDto::of)
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
    * param : 토큰 주인이 팔로잉할 memberId
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

    /**
     * 차단할 사람의 ID 값만 넘겨주면 토큰 주인 ID로 차단 등록
     * param : 토큰 주인이 차단할 memberId
     * */
    @Transactional
    public CommandResponseDto deleteFollowing(Long followedMemberId) {
        if(memberRepository.deleteFollowing(followedMemberId)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no following");
        }
    }

    /**
     * 차단할 사람의 ID 값만 넘겨주면 토큰 주인 ID로 차단 등록
     * 차단 당한 사람이 토큰 주인을 팔로잉하고 있으면 팔로잉 삭제
     * 비즈니스 로직이 repository에 들어옴 -> 수정 필요
     * param : 토큰 주인이 차단할 memberId
     * */
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
        List<MemberInfoDto> members = memberRepository.findBlockingWho(memberId).stream()
                .map(MemberInfoDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    /**
     * 누구를 팔로우 하고 있는지 조회
     * param: 팔로우 조회할 memberId
     * */
    public MemberListDto getFollowingWho(Long memberId) {
        List<MemberInfoDto> members = memberRepository.findFollowingWho(memberId).stream()
                .map(MemberInfoDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    public MemberListDto getWhoIsFollower(Long memberId) {
        List<MemberInfoDto> members = memberRepository.findWhoIsFollower(memberId).stream()
                .map(MemberInfoDto::of)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    /**
     * 토큰 주인을 기준으로 다른 멤버의 정보를 조회
     * Params : 정보를 조회할 Member
     * Throws : NullPointerException - 멤버가 존재하지 않을 때 (해당 memberId 없음)
     * */
    public MemberInfoDto getMemberInfo(Member member) throws NullPointerException{
        FriendshipType friendship;
        Member reqMember = memberRepository.find(SecurityUtil.getCurrentMemberId());

        List<Member> followedMembers = reqMember.getFollowings().stream()
                .map(f -> f.getFollowedMember())
                .collect(Collectors.toList());

        List<Member> blockedMembers = reqMember.getBlockings().stream()
                .map(b -> b.getBlockedMember())
                .collect(Collectors.toList());

        if(followedMembers.contains(member)){
            friendship = FriendshipType.FOLLOW;
        } else if (blockedMembers.contains(member)) {
            friendship = FriendshipType.BLOCK;
        } else {
            friendship = FriendshipType.NONE;
        }

        MemberInfoDto dto = MemberInfoDto.of(member);
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

    @Transactional
    public CommandResponseDto deleteMember() {
        if (memberRepository.deleteMember()){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no refresh token");
        }
    }

    public InterestListResponseDto getInterestList() {
        Interest root = interestRepository.findBySubject("root");
        List<Interest> interests = interestRepository.findByParent(root);
        return InterestListResponseDto.builder()
                .interests(interests.stream().map(interest -> interest.getSubject()).collect(Collectors.toList()))
                .build();
    }

    public boolean nicknameCheck(String nickname) {
        return memberRepository.duplicationCheck(nickname);
    }
}
