package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 회원가입 시, firebase token과 회원 정보를 모두 받기 위한 Dto
 * */
public class MemberJoinRequestDto {

    @NotEmpty
    private String firebaseToken;
    private Long id;

    @Size(max=10)
    private String nickname;
    @NotNull
    private CountryType country;
    @NotNull
    private LanguageType language;
    private String introduction;
    private AvatarType avatar;
    private OnStatusType onlineStatus;
    private OnStatusType entranceStatus;
    private EntranceRoom entranceRoom;
    private FriendshipType friendship;

    private List<String> interests;

    public static MemberJoinRequestDto of(Member member){
        return MemberJoinRequestDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .country(member.getCountryType())
                .language(member.getLanguageType())
                .introduction(member.getIntroduction())
                .avatar(member.getAvatarType())
                .onlineStatus(member.getOnlineStatus())
                .entranceStatus(member.getEntranceStatus())
                .entranceRoom(member.getEntranceRoom())
                .build();
    }

    public MemberInfoDto toMemberInfoRequestDto() {
        return MemberInfoDto.builder()
                .id(this.getId())
                .nickName(this.getNickname())
                .country(this.getCountry())
                .language(this.getLanguage())
                .introduction(this.getIntroduction())
                .avatar(this.getAvatar())
                .onlineStatus(this.getOnlineStatus())
                .entranceStatus(this.getEntranceStatus())
                .entranceRoom(this.getEntranceRoom())
                .interests(this.getInterests())
                .build();
    }
}
