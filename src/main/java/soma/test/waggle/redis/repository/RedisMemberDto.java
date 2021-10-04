package soma.test.waggle.redis.repository;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Builder
@AllArgsConstructor
public class RedisMemberDto implements Serializable {

    private String memberId;

//    private LocalDateTime createdAt;
}
