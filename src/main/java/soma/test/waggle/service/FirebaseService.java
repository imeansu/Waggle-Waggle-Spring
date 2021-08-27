package soma.test.waggle.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soma.test.waggle.dto.FirebaseTokenDto;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseToken firebaseAuthentication(FirebaseTokenDto idToken) throws FirebaseAuthException {
        // idToken comes from the client app (shown above)
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken.getFirebaseToken());
        return decodedToken;
    }

}
