package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.*;
import soma.test.waggle.entity.Country;
import soma.test.waggle.entity.Language;
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

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    // ambiguous uri and anti pattern
//    @GetMapping("/{email}")
//    public ResponseEntity<MemberResponseDto> getMemberInfo(@PathVariable String email) {
//        return ResponseEntity.ok(memberService.getMemberInfo(email));
//    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoRequestDto> findMemberById(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }

    @PutMapping("/edit-member")
    public ResponseEntity<Object> editMemberInfo(@RequestBody MemberInfoRequestDto memberInfoRequestDto){
        return ResponseEntity.ok(memberService.putMemberInfo(memberInfoRequestDto));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommandResponseDto> deleteMember(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.deleteMember());
    }

    @GetMapping("/online")
    public ResponseEntity<OnlineMemberResponseDto> onlineMemberList(){
        return ResponseEntity.ok(memberService.getOnlineMembers());
    }

    @PostMapping("/{followed-user-id}/follow")
    public ResponseEntity<Object> newFollowing(@PathVariable Long followedUserId){
        return ResponseEntity.ok(memberService.createFollowing(followedUserId));
    }

    @DeleteMapping("/{followed-user-id}/unfollow")
    public ResponseEntity<CommandResponseDto> unfollow(@PathVariable Long followedUserId){
        return ResponseEntity.ok(memberService.deleteFollowing(followedUserId));
    }

    @GetMapping("/{user-id}/following")
    public ResponseEntity<MemberListDto> following(@PathVariable Long userId){
        return ResponseEntity.ok(memberService.getFollowingWho(userId));
    }

    @GetMapping("/{user-id}/follower")
    public ResponseEntity<MemberListDto> follower(@PathVariable Long userId){
        return ResponseEntity.ok(memberService.getWhoIsFollower(userId));
    }

    @PostMapping("/{blocked-user-id}/block")
    public ResponseEntity<Object> newBlocking(@PathVariable Long blockedUserId){
        return ResponseEntity.ok(memberService.createBlocking(blockedUserId));
    }

    @DeleteMapping("/{blocked-user-id}/unblock")
    public ResponseEntity<CommandResponseDto> unBlock(@PathVariable Long blockedUserId){
        return ResponseEntity.ok(memberService.deleteBlocking(blockedUserId));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<CommandResponseDto> logout(){
        return ResponseEntity.ok(memberService.logout());
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("================sucess===============");
        return "hello";
    }

    @GetMapping("/basics/country-list")
    public ResponseEntity<CountryListResponseDto> country(){
        List<Country> countries = Arrays.asList(Country.class.getEnumConstants());
        return ResponseEntity.ok(CountryListResponseDto.builder()
                .countries(countries)
                .build());
    }

    @GetMapping("/basics/language-list")
    public ResponseEntity<LanguageListResponseDto> language(){
        List<Language> languages = Arrays.asList(Language.class.getEnumConstants());
        return ResponseEntity.ok(LanguageListResponseDto.builder()
                .languages(languages)
                .build());
    }

    @GetMapping("/basics/interest-list")
    public ResponseEntity<InterestListResponseDto> interest(){
        return ResponseEntity.ok(memberService.getInterestList());
    }

    @GetMapping("/basics/nickname-check")
    public ResponseEntity<Map> nicknameDuplicationCheck(@RequestParam("nickname") String nickname){
        boolean result = memberService.nicknameCheck(nickname);
        Map<String, Boolean> res = new HashMap();
        res.put("isValid", result);
        return ResponseEntity.ok(res);
    }

}
