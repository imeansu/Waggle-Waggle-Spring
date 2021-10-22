package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.Blocking;
import soma.test.waggle.entity.Following;
import soma.test.waggle.entity.Member;
import soma.test.waggle.util.SecurityUtil;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private final EntityManager em;

    public boolean saveFollowing(Following following){
        em.persist(following);
        return true;
    }

    /**
     * 팔로우 취소
     * param : 토큰 주인이 팔로우 취소할 memberId
     * */
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

    public boolean deleteFollowing(Following following){
        em.remove(following);
        return true;
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

    public boolean saveBlocking(Blocking blocking){
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

    /**
     * 누구에게 팔로우 당하고 있는지 조회
     * param: 팔로워 조회할 memberId
     * */
    public List<Member> findWhoIsFollower(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery(
                "select m from Following f" +
                        " join f.followingMember m" +
                        " where f.followedMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 누구를 차단 하고 있는지 조회
     * param: 차단 조회할 memberId
     * */
    public List<Member> findBlockingWho(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery(
                "select m from Blocking b" +
                        " join b.blockedMember m" +
                        " where b.blockingMember = :member", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 차단 엔티티 조회
     * */
    public List<Blocking> findBlockingWho(Long blockingId, Long blockedId) {
        Member blockingMember = em.find(Member.class, blockingId);
        Member blockedMember = em.find(Member.class, blockedId);
        return em.createQuery(
                "select b from Blocking b" +
                        " join b.blockedMember m" +
                        " where b.blockingMember = :blockingMember" +
                        " and b.blockedMember = :blockedMember", Blocking.class)
                .setParameter("blockingMember", blockingMember)
                .setParameter("blockedMember", blockedMember)
                .getResultList();
    }
}
