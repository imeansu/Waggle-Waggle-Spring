package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.entity.Blocking;
import soma.test.waggle.entity.Following;
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

    public Member find(Long id){
        return  em.find(Member.class, id);
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
        return em.createQuery("select m from Following f join f.followedMember m where f.followingMember = :member and m.onlineStatus = :status", Member.class)
                .setParameter("member", member)
                .setParameter("status", OnStatus.Y)
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

    @Transactional
    public boolean createFollowing(Following following) {
        List<Member> blockedMembers = em.createQuery(
                "select blockedMember from Blocking b" +
                        " join b.blockedMember blockedMember" +
                        " join b.blockingMember blockingMember" +
                        " where blockingMember = :followedMember" +
                        " and blockedMember = :followingMember", Member.class)
                .setParameter("followedMember", following.getFollowedMember())
                .setParameter("followingMember", following.getFollowingMember())
                .getResultList();
        if (blockedMembers.contains(following.getFollowingMember())){
            return false;
        }
        em.persist(following);
        return true;
    }

    public List<Member> findFollowingWho(Long userId){
        Member member = em.find(Member.class, userId);
        return em.createQuery(
                "select m from Following f" +
                        " join f.followedMember m" +
                        " where f.followingMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Member> findFollowingWho(Long followingId, Long followedId){
        Member followingMember = em.find(Member.class, followingId);
        Member followedMember = em.find(Member.class, followedId);
        return em.createQuery(
                "select f from Following f" +
                        " join f.followedMember m" +
                        " where f.followingMember = :followingMember" +
                        " and f.followedMember = :followedMember", Member.class)
                .setParameter("followingMember", followingMember)
                .setParameter("followedId", followedId)
                .getResultList();
    }

    public List<Member> findBlockMember(Long id){
        Member member = em.find(Member.class, id);
        return em.createQuery(
                "select m from Blocking b" +
                        " join b.blockedMember m" +
                        " where b.blockingMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    @Transactional
    public boolean createBlocking(Blocking blocking) {
        Long blockingId = blocking.getBlockingMember().getId();
        Long blockedId = blocking.getBlockedMember().getId();

        List<Member> blockingFollow = findFollowingWho(blockingId, blockedId);
        if(blockingFollow.size() > 0 ){
            em.remove(blockingFollow.get(0));
        }

        List<Member> blockedFollow = findFollowingWho(blockedId, blockingId);
        if(blockedFollow.size() > 0 ){
            em.remove(blockedFollow.get(0));
        }

        em.persist(blocking);
        return true;
    }

    public List<Member> findWhoIsFollower(Long userId) {
        Member member = em.find(Member.class, userId);
        return em.createQuery(
                "select m from Following f" +
                        " join f.followingMember m" +
                        " where f.followedMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }
}
