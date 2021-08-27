package soma.test.waggle.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class World {

    @Id @GeneratedValue
    @Column(name = "world_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @OneToMany(mappedBy = "world")
    private List<WorldRoom> worldRooms = new ArrayList<>();

    @NotNull
    private String description;

}
