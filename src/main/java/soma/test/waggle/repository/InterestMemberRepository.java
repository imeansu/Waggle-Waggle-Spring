package soma.test.waggle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soma.test.waggle.entity.InterestMember;

public interface InterestMemberRepository extends JpaRepository<InterestMember, Long> {
}
