package soma.test.waggle.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interest를 부모 - 자식 계층 관계로 구성
 * Category 처럼 사용할 수 있음
 * */
@Entity @Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interest {

    @Id @GeneratedValue
    @Column(name = "basic_interest_id")
    private Long id;

    private String subject;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Interest parent;

    @OneToMany(mappedBy = "parent")
    private List<Interest> child = new ArrayList<>();

    public Interest returnThis(){
        return this;
    }

}
