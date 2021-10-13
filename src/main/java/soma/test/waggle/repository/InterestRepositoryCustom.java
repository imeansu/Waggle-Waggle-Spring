package soma.test.waggle.repository;

import org.springframework.data.repository.query.Param;
import soma.test.waggle.entity.Interest;

import java.util.List;
import java.util.Map;

public interface InterestRepositoryCustom {
    Map<String, Interest> findInterestMap(List<String> interests);
}
