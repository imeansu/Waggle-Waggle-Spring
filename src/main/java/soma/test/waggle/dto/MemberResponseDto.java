package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import soma.test.waggle.entity.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 삭제 및 대체 예정
 * */
public class MemberResponseDto {
    private String email;

    public static MemberResponseDto of(Member member){
        return new MemberResponseDto(member.getEmail());
    }
}
