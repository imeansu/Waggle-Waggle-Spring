package soma.test.waggle.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interest를 부모 - 자식 계층 관계로 구성
 * Category 처럼 사용할 수 있음
 * */
@Entity
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

    //==연관관계 메서드==//
    public void addChildInterest(Interest child){
        this.child.add(child);
        child.setParent(this);
    }

}
