package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soma.test.waggle.dto.MemberDto;
import soma.test.waggle.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public MemberDto save(MemberDto memberDto){
        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setName(memberDto.getName());
        member.setFireBaseId(memberDto.getFirebaseId());
        em.persist(member);
        return memberDto;
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> findByFirebaseId(String id){
        return Optional.ofNullable(em.createQuery("select m from Member m where m.firebaseId = :firebaseId", Member.class)
                .setParameter("firebaseId", id)
                .getSingleResult());
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findByEmail(String email){
        return Optional.ofNullable(em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult());
    };

}
