package soma.test.waggle.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FirebaseResponseDto {

    private String isNewMember;
    private Long memberId;
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long accessTokenExpiresIn;

    public void setIsNewMember(String isNewMember) {
        this.isNewMember = isNewMember;
    }

    public void setToken(TokenDto tokenDto) {
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.grantType = tokenDto.getGrantType();
        this.accessTokenExpiresIn = tokenDto.getAccessTokenExpiresIn();
    }
}
