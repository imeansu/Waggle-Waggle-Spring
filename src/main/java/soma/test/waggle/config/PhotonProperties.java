package soma.test.waggle.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PhotonProperties {

    @Value("${photon.AppId}")
    public String photonAppId;

}
