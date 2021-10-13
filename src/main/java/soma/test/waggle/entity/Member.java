package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import soma.test.waggle.dto.InitMemberDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    private Country country;

    @Enumerated(value = EnumType.STRING)
    private Language language;

    private String introduction;

    @OneToMany(mappedBy = "member")
    private List<InterestMember> interests = new ArrayList<>();

//    @NotNull
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private Avatar avatar;

    @Enumerated(value = EnumType.STRING)
    private OnStatus onlineStatus;
    @Enumerated(value = EnumType.STRING)
    private OnStatus entranceStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrance_id")
    private EntranceRoom entranceRoom;

    @OneToMany(mappedBy = "followingMember", cascade = CascadeType.ALL)
    private List<Following> followings = new ArrayList<>();

    @OneToMany(mappedBy = "blockingMember")
    private List<Blocking> blockings = new ArrayList<>();

    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String email, String name, String password, String firebaseId, Authority authority, LocalDate date) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.firebaseId = firebaseId;
        this.authority = authority;
        this.date = date;
    }

    public Member(InitMemberDto dto) {
        this.email = dto.getEmail();
        this.name = dto.getName();
        this.firebaseId = dto.getFirebaseId();
    }

}
