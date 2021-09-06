package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.CommandResponseDto;
import soma.test.waggle.entity.Blocking;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.entity.OnStatus;
import soma.test.waggle.util.SecurityUtil;

import javax.persistence.EntityManager;
import java.util.Arrays;
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

    public boolean deleteFollowing(Long followedUserId) {
        Member deleteMember = em.find(Member.class, followedUserId);
        Member followingMember = em.find(Member.class, SecurityUtil.getCurrentMemberId());
        List<Following> deleteFollowing = em.createQuery(
                "select f from Following f" +
                        " join f.followingMember followingMember" +
                        " join f.followedMember followedMember" +
                        " where followingMember = :followingMember" +
                        " and followedMember = :followedMember", Following.class)
                .setParameter("followingMember", followingMember)
                .setParameter("followedMember", deleteMember)
                .getResultList();
        if (deleteFollowing.size() == 0){
            return false;
        } else {
            em.remove(deleteFollowing.get(0));
            return true;
        }
    }

    public List<Following> findAllFollowing(){
        return em.createQuery("select f from Following f").getResultList();
    }

    public List<Member> findFollowingWho(Long memberId){
        Member member = em.find(Member.class, memberId);
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
                "select m from Following f" +
                        " join f.followedMember m" +
                        " where f.followingMember = :followingMember" +
                        " and f.followedMember = :followedMember", Member.class)
                .setParameter("followingMember", followingMember)
                .setParameter("followedMember", followedMember)
                .getResultList();
    }

    public List<Following> findFollowing(Long followingId, Long followedId){
        Member followingMember = em.find(Member.class, followingId);
        Member followedMember = em.find(Member.class, followedId);
        return em.createQuery(
                "select f from Following f" +
                        " join f.followedMember m" +
                        " where f.followingMember = :followingMember" +
                        " and f.followedMember = :followedMember", Following.class)
                .setParameter("followingMember", followingMember)
                .setParameter("followedMember", followedMember)
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

    public boolean createBlocking(Blocking blocking) {
        Long blockingId = blocking.getBlockingMember().getId();
        Long blockedId = blocking.getBlockedMember().getId();

        List<Member> blockingFollow = findFollowingWho(blockingId, blockedId);
        if(blockingFollow.size() > 0 ){
            em.remove(findFollowing(blockingId, blockedId).get(0));
        }

        List<Member> blockedFollow = findFollowingWho(blockedId, blockingId);
        if(blockedFollow.size() > 0 ){
            em.remove(findFollowing(blockedId, blockingId).get(0));
        }

        em.persist(blocking);
        return true;
    }

    public boolean deleteBlocking(Long blockedUserId) {
        Member deleteMember = em.find(Member.class, blockedUserId);
        Member blockingMember = em.find(Member.class, SecurityUtil.getCurrentMemberId());
        List<Blocking> deleteBlocking = em.createQuery(
                "select b from Blocking b" +
                        " join b.blockingMember blockingMember" +
                        " join b.blockedMember blockedMember" +
                        " where blockingMember = :blockingMember" +
                        " and blockedMember = :blockedMember", Blocking.class)
                .setParameter("blockingMember", blockingMember)
                .setParameter("blockedMember", deleteMember)
                .getResultList();
        if (deleteBlocking.size() == 0){
            return false;
        } else {
            em.remove(deleteBlocking.get(0));
            return true;
        }
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


    public List<Member> findBlockingWho(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery(
                "select m from Blocking b" +
                        " join b.blockedMember m" +
                        " where b.blockingMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Member> findBlockingWho(Long blockingId, Long blockedId) {
        Member blockingMember = em.find(Member.class, blockingId);
        Member blockedMember = em.find(Member.class, blockedId);
        return em.createQuery(
                "select b from Blocking b" +
                        " join b.blockedMember m" +
                        " where b.blockingMember = :blockingMember" +
                        " and b.blockedMember = :blockedMember", Member.class)
                .setParameter("blockingMember", blockingMember)
                .setParameter("blockedId", blockedMember)
                .getResultList();
    }

}
