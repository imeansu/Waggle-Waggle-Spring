package soma.test.waggle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Interest;

import java.util.List;


@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    public Interest findBySubject(String subject);
    public List<Interest> findByParent(Interest interest);
}
