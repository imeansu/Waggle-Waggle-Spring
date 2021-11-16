package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.error.ErrorCode;
import soma.test.waggle.error.exception.BlockedMemberException;
import soma.test.waggle.error.exception.DuplicatedRequestException;
import soma.test.waggle.error.exception.MemberNotFoundException;
import soma.test.waggle.error.exception.WaggleWaggleException;
import soma.test.waggle.redis.repository.RedisConversationCacheRepositoryImpl;
import soma.test.waggle.repository.FriendshipRepository;
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
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;
    private final InterestRepository interestRepository;
    private final InterestMemberRepository interestMemberRepository;
    private final RedisConversationCacheRepositoryImpl redisRepository;

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
    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfoWithFriendship(Long memberId) {
        try{
            return getMemberInfoWithFriendship(memberRepository.find(memberId));
        } catch (Exception e){
            throw new MemberNotFoundException(e.getMessage());
        }
    }

    /**
     * 토큰 주인 ID의 멤버 정보 수정
     * */
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

    @Transactional(readOnly = true)
    public OnlineMemberResponseDto getOnlineMembers() {
        List<Long> blockingMemberIds = getBlockingWho(SecurityUtil.getCurrentMemberId()).getMembers().stream()
                .map(MemberInfoDto::getId)
                .collect(Collectors.toList());
        List<Long> blockedByWhoIds = friendshipRepository.findBlockedByWho(SecurityUtil.getCurrentMemberId()).stream()
                .map(MemberInfoDto::of)
                .map(MemberInfoDto::getId)
                .collect(Collectors.toList());

        List<MemberInfoDto> followingMembers = memberRepository.getOnlineFollowingMembers().stream()
                .map(MemberInfoDto::of)
                .filter(dto -> !blockingMemberIds.contains(dto.getId()))
                .filter(dto -> !blockedByWhoIds.contains(dto.getId()))
                .map(dto -> {
                    dto.setFriendship(FriendshipType.FOLLOW);
                    return dto;
                })
                .collect(Collectors.toList());

        List<Long> followingMemberId = followingMembers.stream()
                .map(dto -> dto.getId())
                .collect(Collectors.toList());

        List<MemberInfoDto> onlineMembers = memberRepository.getOnlineMembers().stream()
                .map(MemberInfoDto::of)
                .filter(dto -> !followingMemberId.contains(dto.getId()))
                .filter(dto -> !blockingMemberIds.contains(dto.getId()))
                .filter(dto -> !blockedByWhoIds.contains(dto.getId()))
                .map(dto -> {
                    dto.setFriendship(FriendshipType.NONE);
                    return dto;
                })
                .collect(Collectors.toList());

        // 임시 online member 가져오기
        List<Long> cacheOnlineMemberIds = redisRepository.getOnlineMemberIds().stream().map(Long::parseLong).collect(Collectors.toList());
        List<MemberInfoDto> cacheOnlineMemberDtos = cacheOnlineMemberIds.stream()
                .map(memberId -> MemberInfoDto.of(memberRepository.find(memberId)))
                .filter(dto -> !followingMemberId.contains(dto.getId()))
                .filter(dto -> !blockingMemberIds.contains(dto.getId()))
                .filter(dto -> !blockedByWhoIds.contains(dto.getId()))
                .map(dto -> {
                    dto.setFriendship(FriendshipType.NONE);
                    return dto;
                })
                .collect(Collectors.toList());
        // 중복 제거 필요함
        onlineMembers.addAll(cacheOnlineMemberDtos);

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
     * throws: BlockedMemberException - 차단된 경우, DuplicatedRequestException - 이미 팔로우된 경우
     * */
    public CommandResponseDto createFollowingWithCheck(Long followedMemberId){
        Long reqMemberId = SecurityUtil.getCurrentMemberId();
        Following following = Following.builder()
                .followingMember(memberRepository.find(reqMemberId))
                .followedMember(memberRepository.find(followedMemberId))
                .dateTime(LocalDateTime.now())
                .build();

        // 상대방의 차단 여부 확인
        List<Long> blockingCheck = friendshipRepository.findBlockingWho(followedMemberId).stream()
                .map(member -> member.getId())
                .collect(Collectors.toList());
        if (blockingCheck.contains(reqMemberId)){
            throw new BlockedMemberException(ErrorCode.BLOCKED_MEMBER);
        }

        // 나의 차단 여부 확인
        List<Long> blockingCheckMe = friendshipRepository.findBlockingWho(reqMemberId).stream()
                .map(member -> member.getId())
                .collect(Collectors.toList());
        if (blockingCheckMe.contains(followedMemberId)){
            throw new BlockedMemberException(ErrorCode.BLOCKED_MEMBER);
        }

        // 중복 요청 여부 확인
        List<Following> followingCheck = friendshipRepository.findFollowing(reqMemberId, followedMemberId);
        if (followingCheck.size() > 0){
            throw new DuplicatedRequestException(ErrorCode.DUPLICATED_REQUEST_EXCEPTION);
        }

        if(friendshipRepository.saveFollowing(following)){
            return new CommandResponseDto("ok");
        } else {
            throw new WaggleWaggleException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 차단할 사람의 ID 값만 넘겨주면 토큰 주인 ID로 차단 등록
     * param : 토큰 주인이 차단할 memberId
     * */
    public CommandResponseDto deleteFollowing(Long followedMemberId) {
        if(friendshipRepository.deleteFollowing(followedMemberId)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no following");
        }
    }

    /**
     * 차단할 사람의 ID 값만 넘겨주면 토큰 주인 ID로 차단 등록
     * 차단 당한 사람이 토큰 주인을 팔로잉하고 있으면 팔로잉 삭제
     * 차단 당하는 사람을 토큰 주인이 팔로잉 하고 있어도 팔로잉 삭제
     * param : 토큰 주인이 차단할 memberId
     * throw : DuplicatedRequestException - 이미 차단된 경우
     * */
    public CommandResponseDto createBlocking(Long blockedMemberId) {
        Long reqMemberId = SecurityUtil.getCurrentMemberId();
        Blocking blocking = Blocking.builder()
                .blockingMember(memberRepository.find(reqMemberId))
                .blockedMember(memberRepository.find(blockedMemberId))
                .dateTime(LocalDateTime.now())
                .build();

        // 상대방의 follow 취소
        List<Following> followingCheck = friendshipRepository.findFollowing(blockedMemberId, reqMemberId);
        if(followingCheck.size() > 0){
            friendshipRepository.deleteFollowing(followingCheck.get(0));
        }

        // 토큰 주인의 follow 취소
        List<Following> followingCheckMe = friendshipRepository.findFollowing(reqMemberId, blockedMemberId);
        if(followingCheckMe.size() > 0){
            friendshipRepository.deleteFollowing(followingCheckMe.get(0));
        }

        // 중복 요청 여부 확인
        List<Blocking> blockingCheck = friendshipRepository.findBlockingWho(reqMemberId, blockedMemberId);
        if (blockingCheck.size() > 0){
            throw new DuplicatedRequestException(ErrorCode.DUPLICATED_REQUEST_EXCEPTION);
        }

        if(friendshipRepository.saveBlocking(blocking)){
            return new CommandResponseDto("ok");
        } else {
            throw new WaggleWaggleException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    public CommandResponseDto deleteBlocking(Long blockedMemberId) {
        if(friendshipRepository.deleteBlocking(blockedMemberId)){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no blocking");
        }
    }

    /**
     * 누구를 차단 하고 있는지 조회
     * param: 차단 조회할 memberId
     * */
    public MemberListDto getBlockingWho(Long memberId){
        List<MemberInfoDto> members = friendshipRepository.findBlockingWho(memberId).stream()
                .map(MemberInfoDto::of)
                .map(dto -> {
                    dto.setFriendship(FriendshipType.BLOCK);
                    return dto;
                })
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    /**
     * 누구에게 차단당하고 있는지
     * param: 차단 당함 조회할 memberId
     * */
    public List<MemberInfoDto> getBlockedByWho(Long memberId){
        List<MemberInfoDto> members = friendshipRepository.findBlockedByWho(memberId).stream()
                .map(MemberInfoDto::of)
                .map(dto -> {
                    dto.setFriendship(FriendshipType.BLOCK);
                    return dto;
                })
                .collect(Collectors.toList());
        return members;
    }

    /**
     * 누구를 팔로우 하고 있는지 조회
     * param: 팔로우 조회할 memberId
     * */
    @Transactional(readOnly = true)
    public MemberListDto getFollowingWho(Long memberId) {
        List<MemberInfoDto> members = friendshipRepository.findFollowingWho(memberId).stream()
                .map(this::getMemberInfoWithFriendship)
                .collect(Collectors.toList());
        return MemberListDto.builder()
                .size(members.size())
                .members(members)
                .build();
    }

    @Transactional(readOnly = true)
    public MemberListDto getWhoIsFollower(Long memberId) {
        List<MemberInfoDto> members = friendshipRepository.findWhoIsFollower(memberId).stream()
                .map(this::getMemberInfoWithFriendship)
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
    public MemberInfoDto getMemberInfoWithFriendship(Member member) throws NullPointerException{
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

    public CommandResponseDto logout() {
        if(memberRepository.deleteRefreshToken()){
            return new CommandResponseDto("ok");
        }
        else{
            return new CommandResponseDto("no refresh token");
        }
    }

    public CommandResponseDto deleteMember() {
        if (memberRepository.deleteMember()){
            return new CommandResponseDto("ok");
        } else {
            return new CommandResponseDto("no refresh token");
        }
    }

    @Transactional(readOnly = true)
    public InterestListResponseDto getInterestList() {
        Interest root = interestRepository.findBySubject("root");
        List<Interest> interests = interestRepository.findByParent(root);
        return InterestListResponseDto.builder()
                .interests(interests.stream().map(interest -> interest.getSubject()).collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public boolean nicknameCheck(String nickname) {
        return memberRepository.duplicationCheck(nickname);
    }
}
