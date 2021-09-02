package soma.test.waggle.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import soma.test.waggle.entity.Authority;
import soma.test.waggle.entity.Member;

@Getter @Setter
public class InitMemberDto {

    private String firebaseId;
    private String email;
    private String name;

}
