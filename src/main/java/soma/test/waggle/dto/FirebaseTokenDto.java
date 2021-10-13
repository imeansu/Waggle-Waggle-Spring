package soma.test.waggle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class FirebaseTokenDto {
    @NotEmpty
    private String firebaseToken;
}
