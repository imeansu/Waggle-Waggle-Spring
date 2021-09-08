package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.*;
import soma.test.waggle.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @GetMapping("/{email}")
    public ResponseEntity<MemberResponseDto> getMemberInfo(@PathVariable String email) {
        return ResponseEntity.ok(memberService.getMemberInfo(email));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoRequestDto> findMemberById(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.findMemberById(memberId));
    }

    @PutMapping("/edit-member")
    public ResponseEntity<Object> editMemberInfo(@RequestBody MemberInfoRequestDto memberInfoRequestDto){
        return ResponseEntity.ok(memberService.putMemberInfo(memberInfoRequestDto));
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
}
