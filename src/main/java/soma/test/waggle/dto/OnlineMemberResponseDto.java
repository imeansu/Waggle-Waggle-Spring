package soma.test.waggle.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OnlineMemberResponseDto {

    private int onlineFollowingMemberSize;
    private List<MemberInfoDto> onlineFollowingMembers;

    private int onlineMemberSize;
    private List<MemberInfoDto> onlineMembers;

    @Override
    public String toString() {
        return "OnlineMemberResponseDto{" +
                "onlineFollowingMemberSize=" + onlineFollowingMemberSize +
                ", onlineFollowingMembers=" + onlineFollowingMembers +
                ", onlineMemberSize=" + onlineMemberSize +
                ", onlineMembers=" + onlineMembers +
                '}';
    }
}
