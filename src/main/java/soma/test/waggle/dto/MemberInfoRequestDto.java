package soma.test.waggle.dto;

import lombok.*;
import soma.test.waggle.entity.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoRequestDto {

    private Long id;
    @Size(max=10)
    private String nickName;
    @NotEmpty
    private Country country;
    @NotEmpty
    private Language language;
    private String introduction;
    private Avatar avatar;
    private OnStatus onlineStatus;
    private OnStatus entranceStatus;
    private EntranceRoom entranceRoom;
    private Friendship friendship;
    private List<String> interests;

    public static MemberInfoRequestDto of(Member member){
        return MemberInfoRequestDto.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .country(member.getCountry())
                .language(member.getLanguage())
                .introduction(member.getIntroduction())
                .avatar(member.getAvatar())
                .onlineStatus(member.getOnlineStatus())
                .entranceStatus(member.getEntranceStatus())
                .entranceRoom(member.getEntranceRoom())
                .interests(member.getInterests().stream()
                        .map(interestMember -> interestMember.getInterest().getSubject())
                        .collect(Collectors.toList()))
                .build();
    }

}
