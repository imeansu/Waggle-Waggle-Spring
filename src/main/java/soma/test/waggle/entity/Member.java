package soma.test.waggle.entity;

import lombok.*;
import soma.test.waggle.dto.InitMemberDto;
import soma.test.waggle.type.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Table(name = "member")
@Entity
public class Member implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String firebaseId;

    private String name;
    @NotNull
    private String email;
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    private CountryType countryType;

    @Enumerated(value = EnumType.STRING)
    private LanguageType languageType;

    @Size(max=10000)
    private String introduction;

    @OneToMany(mappedBy = "member")
    private List<InterestMember> interests = new ArrayList<>();

//    @NotNull
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private AvatarType avatarType;

    @Enumerated(value = EnumType.STRING)
    private OnStatusType onlineStatus;
    @Enumerated(value = EnumType.STRING)
    private OnStatusType entranceStatus;

    @OneToMany(mappedBy = "member")
    private List<EntranceRoom> entranceRooms = new ArrayList<>();

    @OneToMany(mappedBy = "followingMember", cascade = CascadeType.ALL)
    private List<Following> followings = new ArrayList<>();

    @OneToMany(mappedBy = "blockingMember")
    private List<Blocking> blockings = new ArrayList<>();

    private String password;

    @Enumerated(EnumType.STRING)
    private AuthorityType authorityType;

    private Long conversationTime = 0L;

    public void addInterestMember(InterestMember interestMember){
        this.interests.add(interestMember);
    }

    @Builder
    public Member(String email, String name, String password, String firebaseId, AuthorityType authorityType, LocalDate date) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.firebaseId = firebaseId;
        this.authorityType = authorityType;
        this.date = date;
    }


    public Member(String email, String name, AvatarType avatarType, String password, String firebaseId, AuthorityType authorityType, LocalDate date, String nickname
                    , OnStatusType onlineStatus, OnStatusType entranceStatus, String introduction, List<InterestMember> interests
    ) {
        this.email = email;
        this.name = name;
        this.avatarType = avatarType;
        this.introduction = introduction;
        this.password = password;
        this.firebaseId = firebaseId;
        this.authorityType = authorityType;
        this.date = date;
        this.nickname = nickname;
        this.onlineStatus = onlineStatus;
        this.entranceStatus = entranceStatus;
        this.interests = interests;

    }

    public Member(InitMemberDto dto) {
        this.email = dto.getEmail();
        this.name = dto.getName();
        this.firebaseId = dto.getFirebaseId();
    }

}
