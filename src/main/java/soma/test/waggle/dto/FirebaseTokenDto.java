package soma.test.waggle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FirebaseTokenDto {
    @JsonProperty(value = "firebase_token")
    private String firebaseToken;

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
