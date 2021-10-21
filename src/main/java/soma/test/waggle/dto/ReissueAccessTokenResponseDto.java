package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueAccessTokenResponseDto {
    private String accessToken;
    private Long accessTokenExpiresIn;
}
