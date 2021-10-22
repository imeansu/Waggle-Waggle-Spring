package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.*;
import soma.test.waggle.type.CountryType;
import soma.test.waggle.type.LanguageType;
import soma.test.waggle.service.MemberService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    /**
     * 토큰 인증 정보를 바탕으로 다른 멤버(memberId) 정보를 조회 (friendship 정보 포함)
     * */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoDto> findMemberById(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }

    /**
     * 토큰 인증 정보를 바탕으로 회원 정보 수정
     * */
    @PutMapping("/edit-member")
    public ResponseEntity<Object> editMemberInfo(@RequestBody MemberInfoDto memberInfoDto){
        return ResponseEntity.ok(memberService.putMemberInfo(memberInfoDto));
    }

    /**
     * 토큰 인증 정보를 바탕으로 회원 탈퇴
     * */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommandResponseDto> deleteMember(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.deleteMember());
    }

    /**
     * 토큰 인증 정보를 바탕으로 온라인 멤버 조회
     * Returns: 팔로우 멤버와 일반 멤버 구분해서 반환
     * */
    @GetMapping("/online")
    public ResponseEntity<OnlineMemberResponseDto> onlineMemberList(){
        return ResponseEntity.ok(memberService.getOnlineMembers());
    }

    /**
     * followed-member-id => 토큰 주인이 팔로우할 멤버
     * */
    @PostMapping("/{followedMemberId}/follow")
    public ResponseEntity<Object> newFollowing(@PathVariable Long followedMemberId){
        return ResponseEntity.ok(memberService.createFollowing(followedMemberId));
    }

    /**
     * followed-member-id => 토큰 주인이 언팔로우할 멤버
     * */
    @DeleteMapping("/{followedMemberId}/unfollow")
    public ResponseEntity<CommandResponseDto> unfollow(@PathVariable Long followedMemberId){
        return ResponseEntity.ok(memberService.deleteFollowing(followedMemberId));
    }

    /**
     * member-id => 토큰 주인이 언팔로우할 멤버
     * */
    @GetMapping("/{memberId}/following")
    public ResponseEntity<MemberListDto> following(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.getFollowingWho(memberId));
    }

    /**
     * member-id => 이 멤버를 누가 팔로우 하고 있는지 조회
     * */
    @GetMapping("/{memberId}/follower")
    public ResponseEntity<MemberListDto> follower(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.getWhoIsFollower(memberId));
    }

    /**
     * blocked-member-id => 토큰 주인이 차단할 멤버
     * */
    @PostMapping("/{blockedMemberId}/block")
    public ResponseEntity<Object> newBlocking(@PathVariable Long blockedMemberId){
        return ResponseEntity.ok(memberService.createBlocking(blockedMemberId));
    }

    /**
     * blocked-member-id => 토큰 주인이 차단 해제할 멤버
     * */
    @DeleteMapping("/{blockedMemberId}/unblock")
    public ResponseEntity<CommandResponseDto> unBlock(@PathVariable Long blockedMemberId){
        return ResponseEntity.ok(memberService.deleteBlocking(blockedMemberId));
    }

    /**
     * 토큰 주인의 refresh token 삭제
     * */
    @DeleteMapping("/logout")
    public ResponseEntity<CommandResponseDto> logout(){
        return ResponseEntity.ok(memberService.logout());
    }

    /**
     * basics: permitAll
     * */
    @GetMapping("/basics/country-list")
    public ResponseEntity<CountryListResponseDto> country(){
        List<CountryType> countries = Arrays.asList(CountryType.class.getEnumConstants());
        return ResponseEntity.ok(CountryListResponseDto.builder()
                .countries(countries)
                .build());
    }

    @GetMapping("/basics/language-list")
    public ResponseEntity<LanguageListResponseDto> language(){
        List<LanguageType> languages = Arrays.asList(LanguageType.class.getEnumConstants());
        return ResponseEntity.ok(LanguageListResponseDto.builder()
                .languages(languages)
                .build());
    }

    @GetMapping("/basics/interest-list")
    public ResponseEntity<InterestListResponseDto> interest(){
        return ResponseEntity.ok(memberService.getInterestList());
    }

    /**
     * isValid = true : 사용 가능한 닉네임
     * 닉네임 관련 @Valid 추가 예정
     * */
    @GetMapping("/basics/nickname-check")
    public ResponseEntity<Map> nicknameDuplicationCheck(@RequestParam("nickname") String nickname){
        boolean result = memberService.nicknameCheck(nickname);
        Map<String, Boolean> res = new HashMap();
        res.put("isValid", result);
        return ResponseEntity.ok(res);
    }

}
