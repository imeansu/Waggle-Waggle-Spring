package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import soma.test.waggle.entity.Authority;
import soma.test.waggle.entity.Member;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequestDto {

    private String email;
    private String password;
    private String firebaseId;
    private String name;
    private LocalDate date;

    public Member toMember(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                .name(name)
                .firebaseId(firebaseId)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .date(date)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(){
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
