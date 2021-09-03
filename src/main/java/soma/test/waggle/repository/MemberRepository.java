package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.dto.InitMemberDto;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.util.SecurityUtil;

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

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public boolean findByFirebaseId(String id){
        String jpql = "select m from Member m where m.firebaseId = :firebaseId";
        List<Member> findMember = em.createQuery(jpql, Member.class)
                .setParameter("firebaseId", id)
                .getResultList();
        if (findMember.size() == 0){
            return false;
        }else{
            return true;
        }
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

    public List<Member> getOnlineFollowingMembers(){
        return getOnlineFollowingMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineFollowingMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
//        System.out.println("SecurityUtil.getCurrentMemberId() = " + SecurityUtil.getCurrentMemberId());
        return em.createQuery("select m from Following f join f.followedMember m where f.followingMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Member> getOnlineMembers(){
        return getOnlineMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery("select m from Member m where m.onlineStatus = :status", Member.class)
                .setParameter("status", OnStatus.Y)
                .getResultList();
    }
}
