package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.AvatarType;
import soma.test.waggle.type.CountryType;
import soma.test.waggle.type.LanguageType;
import soma.test.waggle.type.OnStatusType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MemberInfoRequestDto {

    private Long id;
    @Size(max=10)
    private String nickName;
    @NotEmpty
    private CountryType countryType;
    @NotEmpty
    private LanguageType languageType;
    private String introduction;
    private AvatarType avatarType;
    private OnStatusType onlineStatus;
    private OnStatusType entranceStatus;
    private EntranceRoom entranceRoom;
    private Friendship friendship;
    private List<String> interests;

    public static MemberInfoRequestDto of(Member member) throws NullPointerException{
        return MemberInfoRequestDto.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .countryType(member.getCountryType())
                .languageType(member.getLanguageType())
                .introduction(member.getIntroduction())
                .avatarType(member.getAvatarType())
                .onlineStatus(member.getOnlineStatus())
                .entranceStatus(member.getEntranceStatus())
                .entranceRoom(member.getEntranceRoom())
                .interests(member.getInterests().stream()
                        .map(interestMember -> interestMember.getInterest().getSubject())
                        .collect(Collectors.toList()))
                .build();
    }

}
