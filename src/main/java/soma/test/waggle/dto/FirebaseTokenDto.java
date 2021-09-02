package soma.test.waggle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FirebaseTokenDto {
    @JsonProperty(value = "firebase_token")
    private String firebaseToken;
}
