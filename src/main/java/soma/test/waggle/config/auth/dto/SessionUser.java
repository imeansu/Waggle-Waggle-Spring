package soma.test.waggle.config.auth.dto;

import lombok.Getter;
import soma.test.waggle.domain.user.User;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(User user) {
        this.name = name;
        this.email = email;
    }
}
