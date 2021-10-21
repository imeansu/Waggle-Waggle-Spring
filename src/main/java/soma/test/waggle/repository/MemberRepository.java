package soma.test.waggle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soma.test.waggle.entity.*;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.util.SecurityUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;
    private final RefreshTokenRepository refreshTokenRepository;

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

    public Optional<Member> findByEmail(String email){
        return Optional.ofNullable(em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult());
    }

    public List<Member> findByName(String name){
        return em.createQuery(
                "select m from Member m" +
                        " where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    public Long getMemberIdByFirebaseId(String id){
        String jpql = "select m from Member m where m.firebaseId = :firebaseId";
        List<Member> findMember = em.createQuery(jpql, Member.class)
                .setParameter("firebaseId", id)
                .getResultList();
        if (findMember.size() == 0){
            return -1L;
        }else{
            return findMember.get(0).getId();
        }
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> getOnlineFollowingMembers(){
        return getOnlineFollowingMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineFollowingMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
//        System.out.println("SecurityUtil.getCurrentMemberId() = " + SecurityUtil.getCurrentMemberId());
        return em.createQuery("select m from Following f join f.followedMember m where f.followingMember = :member and m.onlineStatus = :status", Member.class)
                .setParameter("member", member)
                .setParameter("status", OnStatusType.Y)
                .getResultList();
    }

    public List<Member> getOnlineMembers(){
        return getOnlineMembers(SecurityUtil.getCurrentMemberId());
    }
    public List<Member> getOnlineMembers(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return em.createQuery("select m from Member m where m.onlineStatus = :status", Member.class)
                .setParameter("status", OnStatusType.Y)
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

    public boolean deleteRefreshToken() {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByKey(Long.toString(SecurityUtil.getCurrentMemberId()));
        if(refreshToken.isEmpty()){
            return false;
        } else{
            refreshTokenRepository.delete(refreshToken.get());
            return true;
        }
    }

    public boolean deleteMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        try{
            em.remove(find(memberId));
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> findByNickname(String nickname){
        return em.createQuery(
                "select m from Member m" +
                        " where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }

    public boolean duplicationCheck(String nickname) {
        if (findByNickname(nickname).size() == 0){
            return true;
        } else {
            return false;
        }
    }
}
