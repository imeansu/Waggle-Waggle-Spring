package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.MemberInfoRequestDto;
import soma.test.waggle.dto.MemberResponseDto;
import soma.test.waggle.dto.OnlineMemberResponseDto;
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

    @PutMapping("/edit_member")
    public ResponseEntity<Object> editMemberInfo(@RequestBody MemberInfoRequestDto memberInfoRequestDto){
        return ResponseEntity.ok(memberService.putMemberInfo(memberInfoRequestDto));
    }

    @GetMapping("/online")
    public ResponseEntity<OnlineMemberResponseDto> onlineMemberList(){
        return ResponseEntity.ok(memberService.getOnlineMembers());
    }
}
