package soma.test.waggle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Table(name = "member")
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String fireBaseId;

    private String name;
    @NotNull
    private String email;
    private String nickName;

    @Enumerated(value = EnumType.STRING)
    private Country country;

    @Enumerated(value = EnumType.STRING)
    private Language language;

    private String introduction;

//    @NotNull
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private Avatar avatar;

    @Enumerated(value = EnumType.STRING)
    private OnStatus onlineStatus;
    @Enumerated(value = EnumType.STRING)
    private OnStatus entranceStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private EntranceRoom entranceRoom;


    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String email, String password, Authority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

}
