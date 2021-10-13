package soma.test.waggle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Interest;

import java.util.List;
import java.util.Map;


@Repository
public interface InterestRepository extends JpaRepository<Interest, Long>, InterestRepositoryCustom {

    public Interest findBySubject(String subject);
    public List<Interest> findByParent(Interest interest);

}
