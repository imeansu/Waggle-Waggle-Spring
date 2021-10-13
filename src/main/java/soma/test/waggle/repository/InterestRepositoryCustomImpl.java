package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import soma.test.waggle.entity.Interest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InterestRepositoryCustomImpl implements InterestRepositoryCustom {

    private final EntityManager em;

    @Override
    public Map<String, Interest> findInterestMap(List<String> interests) {
        return (Map<String, Interest>) em.createQuery(
                "select i from Interest i" +
                        " where i.subject in :interests")
                .setParameter("interests", interests)
                .getResultList().stream()
                .collect(Collectors.toMap(Interest::getSubject, Interest::returnThis));
    }
}
