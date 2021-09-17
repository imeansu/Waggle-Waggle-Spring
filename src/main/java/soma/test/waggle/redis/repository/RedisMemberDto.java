package soma.test.waggle.redis.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class RedisMemberDto implements Serializable {

    private Long memberId;

    private LocalDateTime createdAt;
}
