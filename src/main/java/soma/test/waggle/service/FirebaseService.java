package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.dto.FirebaseTokenDto;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    public String firebaseAuthentication(FirebaseTokenDto idToken){
        // idToken comes from the client app (shown above)
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken.getFirebaseToken());
            String uid = decodedToken.getUid();
            return uid;
        } catch (Exception e){
            return "fail";
        }
    }

}
