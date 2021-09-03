package soma.test.waggle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import soma.test.waggle.entity.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class MemberInfoRequestDto {

    private Long id;
    private String nickName;
    private Country country;
    private Language language;
    private String introduction;
    private Avatar avatar;
    private OnStatus onlineStatus;
    private OnStatus entranceStatus;
    private EntranceRoom entranceRoom;

    public static MemberInfoRequestDto of(Member member){
        return MemberInfoRequestDto.builder()
                .id(member.getId())
                .nickName(member.getNickName())
                .country(member.getCountry())
                .language(member.getLanguage())
                .introduction(member.getIntroduction())
                .avatar(member.getAvatar())
                .onlineStatus(member.getOnlineStatus())
                .entranceStatus(member.getEntranceStatus())
                .entranceRoom(member.getEntranceRoom())
                .build();
    }

}
